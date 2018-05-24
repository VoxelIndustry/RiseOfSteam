package net.ros.common.tile.machine;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;
import net.ros.common.ROSConstants;
import net.ros.common.card.CardDataStorage;
import net.ros.common.card.CardDataStorage.ECardType;
import net.ros.common.card.CraftCard;
import net.ros.common.card.IPunchedCard;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.EmptyContainer;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.Machines;
import net.ros.common.machine.component.CraftingComponent;
import net.ros.common.machine.component.SteamComponent;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.AutomationModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.steam.ISteamHandler;
import net.ros.common.steam.SteamUtil;
import net.ros.common.util.ItemUtils;

public class TileAssembler extends TileTickingModularMachine implements IContainerProvider
{
    @Getter
    private final BaseProperty<Float> currentProgressProperty;
    @Getter
    @Setter
    private       float               maxProgress;

    private CraftCard craft;
    private ItemStack cached = ItemStack.EMPTY;

    private ItemStack              resultTemp;
    private NonNullList<ItemStack> remainingsTemp;

    // 0 : PunchedCard
    // 1 - 10 : Crafting Ingredients
    // 11 : Result
    // 12 - 20 : Remaining Ingredients
    // 21 - 29 : Craft matrix
    // 30 - 38 : Craft buffer
    public TileAssembler()
    {
        super(Machines.ASSEMBLER);

        this.resultTemp = ItemStack.EMPTY;
        this.currentProgressProperty = new BaseProperty<>(0f, "currentProgressProperty");
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new InventoryModule(this, 39));
        this.addModule(new AutomationModule(this));
        this.addModule(new IOModule(this));
    }

    @Override
    public void update()
    {
        super.update();

        if (this.isClient())
            return;

        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");

        if (!ItemUtils.deepEquals(this.cached, inventory.getStackInSlot(0)))
        {
            this.craft = null;
            this.cached = inventory.getStackInSlot(0).copy();
            if (this.cached.hasTagCompound())
            {
                final IPunchedCard card = CardDataStorage.instance()
                        .read(inventory.getStackInSlot(0).getTagCompound());
                if (card.getID() == ECardType.CRAFT.getID())
                    this.craft = (CraftCard) card;
            }
        }

        ISteamHandler steamTank = this.getModule(SteamModule.class).getInternalSteamHandler();

        if (steamTank.getSteam() >= 0 && this.craft != null)
        {
            boolean empty = false;
            for (int i = 21; i < 30; i++)
            {
                if (inventory.getStackInSlot(i).isEmpty() && !this.craft.getRecipe()[i - 21].isEmpty())
                {
                    boolean completed = false;
                    for (int j = 1; j < 11; j++)
                    {
                        if (!inventory.getStackInSlot(j).isEmpty()
                                && ItemUtils.deepEquals(inventory.getStackInSlot(j), this.craft.getRecipe()[i - 21])
                                || OreDictionary.itemMatches(inventory.getStackInSlot(j), this.craft.getRecipe()[i -
                                        21],
                                true))
                        {
                            inventory.setStackInSlot(i, inventory.extractItem(j, 1, false));
                            completed = true;
                            break;
                        }
                    }
                    if (!completed)
                        empty = true;
                }
            }

            if (!empty && resultTemp.isEmpty())
            {
                InventoryCrafting craftInv = new InventoryCrafting(new EmptyContainer(), 3, 3);
                for (int i = 0; i < 9; i++)
                    craftInv.setInventorySlotContents(i, inventory.getStackInSlot(i + 21));
                ItemStack result = CraftingManager.findMatchingRecipe(craftInv, this.getWorld()).getRecipeOutput();
                this.remainingsTemp = CraftingManager.getRemainingItems(craftInv, this.getWorld());

                if (!result.isEmpty() && canOutput(result))
                {
                    for (int i = 0; i < 9; i++)
                    {
                        inventory.setStackInSlot(i + 30, inventory.getStackInSlot(i + 21));
                        inventory.setStackInSlot(i + 21, ItemStack.EMPTY);
                    }
                    this.setMaxProgress(40);
                    this.resultTemp = result.copy();
                    this.sync();
                }
            }

            if (!resultTemp.isEmpty())
            {
                if (this.getCurrentProgress() < this.getMaxProgress())
                {
                    this.setCurrentProgress(this.getCurrentProgress() + this.getCurrentCraftingSpeed());
                    steamTank.drainSteam(this.getDescriptor().get(SteamComponent.class).getSteamConsumption(), true);
                }
                else
                {
                    if (inventory.getStackInSlot(11).isEmpty())
                        inventory.setStackInSlot(11, resultTemp.copy());
                    else
                        inventory.getStackInSlot(11).grow(resultTemp.getCount());
                    for (int i = 0; i < 9; i++)
                        inventory.setStackInSlot(i + 30, ItemStack.EMPTY);
                    this.resultTemp = ItemStack.EMPTY;
                    this.setCurrentProgress(0);
                    this.remainingsTemp = null;
                }
                this.sync();
            }
        }
    }

    private boolean canOutput(ItemStack result)
    {
        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");

        if (!ItemUtils.canMergeStacks(inventory.getStackInSlot(11), result))
            return false;
        for (ItemStack remain : this.remainingsTemp)
        {
            int toStack = remain.getCount();

            for (int i = 0; i < 9; i++)
            {
                if (toStack <= 0)
                    break;
                if (inventory.getStackInSlot(i + 12).isEmpty())
                    toStack = 0;
                else
                    toStack -= remain.getCount() - ItemUtils.mergeStacks(inventory.getStackInSlot(i + 12), remain,
                            false);
            }
            if (toStack > 0)
                return false;
        }
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setFloat("currentProgressProperty", this.currentProgressProperty.getValue());
        tag.setFloat("maxProgress", this.maxProgress);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.currentProgressProperty.setValue(tag.getFloat("currentProgressProperty"));
        this.maxProgress = tag.getFloat("maxProgress");
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        SteamModule steamEngine = this.getModule(SteamModule.class);

        return new ContainerBuilder("assembler", player).player(player).inventory(8, 106).hotbar(8, 164)
                .addInventory().tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .filterSlot(0, 80, 82, stack -> stack.getItem().equals(ROSItems.PUNCHED_CARD))
                .slot(1, 8, 10).slot(2, 26, 10).slot(3, 8, 28).slot(4, 26, 28).slot(5, 8, 46).slot(6, 26, 46)
                .slot(7, 8, 64).slot(8, 26, 64).slot(9, 8, 82).slot(10, 26, 82).outputSlot(11, 134, 10)
                .outputSlot(12, 152, 10).outputSlot(13, 134, 28).outputSlot(14, 152, 28).outputSlot(15, 134, 46)
                .outputSlot(16, 152, 46).outputSlot(17, 134, 64).outputSlot(18, 152, 64).outputSlot(19, 134, 82)
                .outputSlot(20, 152, 82).outputSlot(21, 62, 10).outputSlot(22, 80, 10).outputSlot(23, 98, 10)
                .outputSlot(24, 62, 28).outputSlot(25, 80, 28).outputSlot(26, 98, 28).outputSlot(27, 62, 46)
                .outputSlot(28, 80, 46).outputSlot(29, 98, 46).displaySlot(30, -1000, 0).displaySlot(31, -1000, 0)
                .displaySlot(32, -1000, 0).displaySlot(33, -1000, 0).displaySlot(34, -1000, 0).displaySlot(35, -1000, 0)
                .displaySlot(36, -1000, 0).displaySlot(37, -1000, 0).displaySlot(38, -1000, 0)
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam)
                .addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.ASSEMBLER.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    public float getCurrentCraftingSpeed()
    {
        return this.getCraftingSpeed() * this.getEfficiency();
    }

    public float getEfficiency()
    {
        return this.getModule(SteamModule.class).getInternalSteamHandler().getPressure() /
                this.getDescriptor().get(SteamComponent.class).getWorkingPressure();
    }

    public float getCurrentProgress()
    {
        return this.currentProgressProperty.getValue();
    }

    public void setCurrentProgress(final float currentProgress)
    {
        this.currentProgressProperty.setValue(currentProgress);
    }

    public int getProgressScaled(final int scale)
    {
        if (this.currentProgressProperty.getValue() != 0 && this.maxProgress != 0)
            return (int) (this.currentProgressProperty.getValue() * scale / this.maxProgress);
        return 0;
    }

    public float getCraftingSpeed()
    {
        return this.getDescriptor().get(CraftingComponent.class).getCraftingSpeed();
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }
}