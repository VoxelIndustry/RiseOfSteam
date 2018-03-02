package net.qbar.common.tile.machine;

import fr.ourten.teabeans.value.BaseProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.CardDataStorage;
import net.qbar.common.card.CardDataStorage.ECardType;
import net.qbar.common.card.CraftCard;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.EmptyContainer;
import net.qbar.common.grid.node.IBelt;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.component.CraftingComponent;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.component.SteamComponent;
import net.qbar.common.multiblock.MultiblockComponent;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.TileMultiblockInventoryBase;
import net.qbar.common.util.ItemUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class TileAssembler extends TileMultiblockInventoryBase implements ITickable
{
    private final CraftingComponent   crafter;
    private final BaseProperty<Float> currentProgress;
    private       float               maxProgress;

    private final SteamTank steamTank;

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
        super(QBarMachines.ASSEMBLER, 39);

        this.crafter = this.getDescriptor().get(CraftingComponent.class);
        this.steamTank = new SteamTank(0, 4000, SteamUtil.BASE_PRESSURE * 2);

        this.resultTemp = ItemStack.EMPTY;
        this.currentProgress = new BaseProperty<>(0f, "currentProgressProperty");
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        if (!ItemUtils.deepEquals(this.cached, this.getStackInSlot(0)))
        {
            this.craft = null;
            this.cached = this.getStackInSlot(0).copy();
            if (this.cached.hasTagCompound())
            {
                final IPunchedCard card = CardDataStorage.instance()
                        .read(this.getStackInSlot(0).getTagCompound());
                if (card.getID() == ECardType.CRAFT.getID())
                    this.craft = (CraftCard) card;
            }
        }

        if (this.steamTank.getSteam() >= 0 && this.craft != null)
        {
            boolean empty = false;
            for (int i = 21; i < 30; i++)
            {
                if (this.getStackInSlot(i).isEmpty() && !this.craft.getRecipe()[i - 21].isEmpty())
                {
                    boolean completed = false;
                    for (int j = 1; j < 11; j++)
                    {
                        if (!this.getStackInSlot(j).isEmpty()
                                && ItemUtils.deepEquals(this.getStackInSlot(j), this.craft.getRecipe()[i - 21])
                                || OreDictionary.itemMatches(this.getStackInSlot(j), this.craft.getRecipe()[i - 21],
                                true))
                        {
                            this.setInventorySlotContents(i, this.decrStackSize(j, 1));
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
                    craftInv.setInventorySlotContents(i, this.getStackInSlot(i + 21));
                ItemStack result = CraftingManager.findMatchingRecipe(craftInv, this.getWorld()).getRecipeOutput();
                this.remainingsTemp = CraftingManager.getRemainingItems(craftInv, this.getWorld());

                if (!result.isEmpty() && canOutput(result))
                {
                    for (int i = 0; i < 9; i++)
                    {
                        this.setInventorySlotContents(i + 30, this.getStackInSlot(i + 21));
                        this.setInventorySlotContents(i + 21, ItemStack.EMPTY);
                    }
                    this.setMaxProgress(40);
                    this.resultTemp = result.copy();
                    this.sync();
                }
            }

            if (!resultTemp.isEmpty())
            {
                if (this.getCurrentProgress() < this.getMaxProgress())
                    this.setCurrentProgress(this.getCurrentProgress() + this.getCurrentCraftingSpeed());
                else
                {
                    if (this.getStackInSlot(11).isEmpty())
                        this.setInventorySlotContents(11, resultTemp.copy());
                    else
                        this.getStackInSlot(11).grow(resultTemp.getCount());
                    for (int i = 0; i < 9; i++)
                        this.setInventorySlotContents(i + 30, ItemStack.EMPTY);
                    this.resultTemp = ItemStack.EMPTY;
                    this.setCurrentProgress(0);
                    this.remainingsTemp = null;
                }
                this.sync();
            }
        }

        final EnumFacing orientation = this.getFacing().getOpposite();

        if (!this.isOutputEmpty() && this.hasBelt(orientation))
        {
            for (int i = 0; i < 10; i++)
            {
                if (!this.getStackInSlot(i + 11).isEmpty())
                {
                    if (this.canInsert(this.getStackInSlot(i + 11), orientation))
                    {
                        this.insert(this.decrStackSize(i + 11, 1), orientation);
                        this.sync();
                    }
                    break;
                }
            }
        }
    }

    private void insert(final ItemStack stack, final EnumFacing facing)
    {
        ((IBelt) this.world.getTileEntity(this.pos.offset(facing))).insert(stack, true);
    }

    private boolean canInsert(final ItemStack stack, final EnumFacing facing)
    {
        final IBelt belt = (IBelt) this.world.getTileEntity(this.pos.offset(facing));

        return belt.insert(stack, false);
    }

    private boolean hasBelt(final EnumFacing facing)
    {
        final TileEntity tile = this.world.getTileEntity(this.pos.offset(facing));

        return tile != null && tile instanceof IBelt;
    }

    private boolean canOutput(ItemStack result)
    {
        if (!ItemUtils.canMergeStacks(this.getStackInSlot(11), result))
            return false;
        for (ItemStack remain : this.remainingsTemp)
        {
            int toStack = remain.getCount();

            for (int i = 0; i < 9; i++)
            {
                if (toStack <= 0)
                    break;
                if (this.getStackInSlot(i + 12).isEmpty())
                    toStack = 0;
                else
                    toStack -= remain.getCount() - ItemUtils.mergeStacks(this.getStackInSlot(i + 12), remain, false);
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

        final NBTTagCompound subTag = new NBTTagCompound();
        this.steamTank.writeToNBT(subTag);
        tag.setTag("steamTank", subTag);

        tag.setFloat("currentProgress", this.currentProgress.getValue());
        tag.setFloat("maxProgress", this.maxProgress);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));

        this.currentProgress.setValue(tag.getFloat("currentProgress"));
        this.maxProgress = tag.getFloat("maxProgress");
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.steamTank.getMaxPressure()));
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("assembler", player).player(player.inventory).inventory(8, 95).hotbar(8, 153)
                .addInventory().tile(this)
                .filterSlot(0, 80, 71, stack -> !stack.isEmpty() && stack.getItem().equals(QBarItems.PUNCHED_CARD))
                .slot(1, 8, -1).slot(2, 26, -1).slot(3, 8, 17).slot(4, 26, 17).slot(5, 8, 35).slot(6, 26, 35)
                .slot(7, 8, 53).slot(8, 26, 53).slot(9, 8, 71).slot(10, 26, 71).outputSlot(11, 134, -1)
                .outputSlot(12, 152, -1).outputSlot(13, 134, 17).outputSlot(14, 152, 17).outputSlot(15, 134, 35)
                .outputSlot(16, 152, 35).outputSlot(17, 134, 53).outputSlot(18, 152, 53).outputSlot(19, 134, 71)
                .outputSlot(20, 152, 71).outputSlot(21, 62, -1).outputSlot(22, 80, -1).outputSlot(23, 98, -1)
                .outputSlot(24, 62, 17).outputSlot(25, 80, 17).outputSlot(26, 98, 17).outputSlot(27, 62, 35)
                .outputSlot(28, 80, 35).outputSlot(29, 98, 35).displaySlot(30, -1000, 0).displaySlot(31, -1000, 0)
                .displaySlot(32, -1000, 0).displaySlot(33, -1000, 0).displaySlot(34, -1000, 0).displaySlot(35, -1000, 0)
                .displaySlot(36, -1000, 0).displaySlot(37, -1000, 0).displaySlot(38, -1000, 0).addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.ASSEMBLER.getUniqueID(), this.world, this.pos.getX(),
                this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        return this.hasCapability(capability, BlockPos.ORIGIN, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        return this.getCapability(capability, BlockPos.ORIGIN, facing);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            MultiblockSide side = QBarMachines.ASSEMBLER.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());
            if (side.getFacing() == EnumFacing.EAST && side.getPos().getX() == 0 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 1)
                return true;
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == this.getFacing())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            MultiblockSide side = QBarMachines.ASSEMBLER.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());
            if (side.getFacing() == EnumFacing.EAST && side.getPos().getX() == 0 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 1)
                return (T) this.steamTank;
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == this.getFacing())
            return (T) this.getInventoryWrapper(facing);
        return super.getCapability(capability, facing);
    }

    private final int[] inputSlots   = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private final int[] outputsSlots = new int[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

    @Override
    public int[] getSlotsForFace(final EnumFacing side)
    {
        return this.inputSlots;
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction)
    {
        if (ArrayUtils.contains(this.inputSlots, index))
            return this.isItemValidForSlot(index, itemStackIn);
        return false;
    }

    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction)
    {
        if (ArrayUtils.contains(this.outputsSlots, index))
            return true;
        return false;
    }

    public float getCurrentCraftingSpeed()
    {
        return this.getCraftingSpeed() * this.getEfficiency();
    }

    public float getEfficiency()
    {
        return this.steamTank.getPressure() / this.getDescriptor().get(SteamComponent.class).getWorkingPressure();
    }

    public BaseProperty<Float> getCurrentProgressProperty()
    {
        return this.currentProgress;
    }

    public float getCurrentProgress()
    {
        return this.currentProgress.getValue();
    }

    public void setCurrentProgress(final float currentProgress)
    {
        this.currentProgress.setValue(currentProgress);
    }

    public float getMaxProgress()
    {
        return this.maxProgress;
    }

    public void setMaxProgress(final float maxProgress)
    {
        this.maxProgress = maxProgress;
    }

    public int getProgressScaled(final int scale)
    {
        if (this.currentProgress.getValue() != 0 && this.maxProgress != 0)
            return (int) (this.currentProgress.getValue() * scale / this.maxProgress);
        return 0;
    }

    public float getCraftingSpeed()
    {
        return this.crafter.getCraftingSpeed();
    }

    public boolean isOutputEmpty()
    {
        for (int i = 0; i < 10; i++)
        {
            if (!this.getStackInSlot(i + 10).isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }
}