package net.ros.common.tile.machine;

import com.google.common.collect.LinkedListMultimap;
import fr.ourten.teabeans.value.BaseListProperty;
import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.ROSConstants;
import net.ros.common.card.CardDataStorage;
import net.ros.common.card.CraftCard;
import net.ros.common.card.FilterCard;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.EmptyContainer;
import net.ros.common.container.IContainerProvider;
import net.ros.common.event.TickHandler;
import net.ros.common.grid.WorkshopMachine;
import net.ros.common.grid.node.ITileWorkshop;
import net.ros.common.gui.MachineGui;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.ClientActionBuilder;
import net.ros.common.network.action.IActionReceiver;

import java.util.List;

public class TileKeypunch extends TileModularMachine implements IContainerProvider, IActionReceiver, ITileWorkshop
{
    @Getter
    private final BaseListProperty<ItemStack> craftStacks;
    @Getter
    private final BaseListProperty<ItemStack> filterStacks;

    private final BaseProperty<Boolean> isCraftTabProperty, canPrintProperty;

    private final InventoryCrafting fakeInv = new InventoryCrafting(new EmptyContainer(), 3, 3);

    @Getter
    @Setter
    private       int                                         grid;
    @Getter
    private final LinkedListMultimap<BlockPos, ITileWorkshop> connectionsMap = LinkedListMultimap.create();

    public TileKeypunch()
    {
        super(Machines.KEYPUNCH);

        this.isCraftTabProperty = new BaseProperty<>(true, "isCraftTabProperty");
        this.canPrintProperty = new BaseProperty<>(false, "canPrintProperty");
        this.craftStacks = new BaseListProperty<>(() -> NonNullList.withSize(9, ItemStack.EMPTY), null);
        this.filterStacks = new BaseListProperty<>(() -> NonNullList.withSize(9, ItemStack.EMPTY), null);

        this.isCraftTabProperty.addListener(obs ->
        {
            if (this.isCraftTabProperty.getValue())
                this.canPrintProperty.setValue(!this.getRecipeResult().isEmpty());
            else
                this.canPrintProperty.setValue(true);
        });

        this.craftStacks.addListener(obs ->
        {
            if (this.isCraftTabProperty.getValue())
                this.canPrintProperty.setValue(!this.getRecipeResult().isEmpty());
        });

        this.grid = -1;
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 2));
        this.getModule(InventoryModule.class).getInventory("basic")
                .addSlotFilter(0, stack -> stack.getItem().equals(ROSItems.PUNCHED_CARD));

        this.addModule(new IOModule(this));
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Grid: " + this.getGrid());

        if (this.getGrid() != -1 && this.getGridObject() != null)
        {
            lines.add("Contains: " + this.getGridObject().getCables().size());
        }
        else
            lines.add("Errored grid!");
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setBoolean("isCraftTab", this.isCraftTabProperty.getValue());

        final NBTTagCompound craftTag = new NBTTagCompound();
        ItemStackHelper.saveAllItems(craftTag, (NonNullList<ItemStack>) this.craftStacks.getModifiableValue());
        tag.setTag("craftTag", craftTag);

        final NBTTagCompound filterTag = new NBTTagCompound();
        ItemStackHelper.saveAllItems(filterTag, (NonNullList<ItemStack>) this.filterStacks.getModifiableValue());
        tag.setTag("filterTag", filterTag);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.isCraftTabProperty.setValue(tag.getBoolean("isCraftTab"));

        final NonNullList<ItemStack> tmpCraft = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag.getCompoundTag("craftTag"), tmpCraft);

        final NonNullList<ItemStack> tmpFilter = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag.getCompoundTag("filterTag"), tmpFilter);

        for (int i = 0; i < 9; i++)
        {
            this.craftStacks.set(i, tmpCraft.get(i));
            this.filterStacks.set(i, tmpFilter.get(i));
        }
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("keypunch", player).player(player).inventory(19, 93).hotbar(19, 151)
                .addInventory().tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .filterSlot(0, 37, 70, stack -> stack.getItem().equals(ROSItems.PUNCHED_CARD))
                .outputSlot(1, 145, 70)
                .syncBooleanValue(this.isCraftTabProperty::getValue, this.isCraftTabProperty::setValue)
                .syncBooleanValue(this.getCanPrintProperty()::getValue, this.getCanPrintProperty()::setValue)
                .syncItemValue(() -> this.getCraftStacks().get(0), stack -> this.getCraftStacks().set(0, stack))
                .syncItemValue(() -> this.getCraftStacks().get(1), stack -> this.getCraftStacks().set(1, stack))
                .syncItemValue(() -> this.getCraftStacks().get(2), stack -> this.getCraftStacks().set(2, stack))
                .syncItemValue(() -> this.getCraftStacks().get(3), stack -> this.getCraftStacks().set(3, stack))
                .syncItemValue(() -> this.getCraftStacks().get(4), stack -> this.getCraftStacks().set(4, stack))
                .syncItemValue(() -> this.getCraftStacks().get(5), stack -> this.getCraftStacks().set(5, stack))
                .syncItemValue(() -> this.getCraftStacks().get(6), stack -> this.getCraftStacks().set(6, stack))
                .syncItemValue(() -> this.getCraftStacks().get(7), stack -> this.getCraftStacks().set(7, stack))
                .syncItemValue(() -> this.getCraftStacks().get(8), stack -> this.getCraftStacks().set(8, stack))
                .syncItemValue(() -> this.getFilterStacks().get(0), stack -> this.getFilterStacks().set(0, stack))
                .syncItemValue(() -> this.getFilterStacks().get(1), stack -> this.getFilterStacks().set(1, stack))
                .syncItemValue(() -> this.getFilterStacks().get(2), stack -> this.getFilterStacks().set(2, stack))
                .syncItemValue(() -> this.getFilterStacks().get(3), stack -> this.getFilterStacks().set(3, stack))
                .syncItemValue(() -> this.getFilterStacks().get(4), stack -> this.getFilterStacks().set(4, stack))
                .syncItemValue(() -> this.getFilterStacks().get(5), stack -> this.getFilterStacks().set(5, stack))
                .syncItemValue(() -> this.getFilterStacks().get(6), stack -> this.getFilterStacks().set(6, stack))
                .syncItemValue(() -> this.getFilterStacks().get(7), stack -> this.getFilterStacks().set(7, stack))
                .syncItemValue(() -> this.getFilterStacks().get(8), stack -> this.getFilterStacks().set(8, stack))
                .addInventory().create();
    }

    public BaseProperty<Boolean> getCraftTabProperty()
    {
        return this.isCraftTabProperty;
    }

    public BaseProperty<Boolean> getCanPrintProperty()
    {
        return this.canPrintProperty;
    }

    public ItemStack getRecipeResult()
    {
        for (int i = 0; i < 9; i++)
            this.fakeInv.setInventorySlotContents(i, this.getCraftStacks().get(i));

        IRecipe recipe = CraftingManager.findMatchingRecipe(this.fakeInv, this.getWorld());
        return recipe != null ? recipe.getRecipeOutput() : ItemStack.EMPTY;
    }

    @Override
    public boolean onRightClick(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.KEYPUNCH.getUniqueID(), this.getWorld(),
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");

        switch (actionID)
        {
            case "SET_TAB":
                this.getCraftTabProperty().setValue(payload.getInteger("tab") == 0);
                this.markDirty();
                break;
            case "SET_STACK":
                if (this.getCraftTabProperty().getValue())
                    this.getCraftStacks().set(payload.getInteger("slot"),
                            new ItemStack(payload.getCompoundTag("itemStack")));
                else
                    this.getFilterStacks().set(payload.getInteger("slot"),
                            new ItemStack(payload.getCompoundTag("itemStack")));
                this.markDirty();
                break;
            case "LOAD_CARD":
                if (this.getCraftTabProperty().getValue())
                {
                    if (inventory.getStackInSlot(0).hasTagCompound() && inventory.getStackInSlot(0).getTagCompound()
                            .getInteger("cardTypeID") == CardDataStorage.ECardType.CRAFT.getID())
                    {
                        final CraftCard card = (CraftCard) CardDataStorage.instance()
                                .read(inventory.getStackInSlot(0).getTagCompound());
                        for (int i = 0; i < card.getRecipe().length; i++)
                            this.getCraftStacks().set(i, card.getRecipe()[i]);
                        this.markDirty();

                    }
                }
                else
                {
                    if (inventory.getStackInSlot(0).hasTagCompound() && inventory.getStackInSlot(0).getTagCompound()
                            .getInteger("cardTypeID") == CardDataStorage.ECardType.FILTER.getID())
                    {
                        final FilterCard card = (FilterCard) CardDataStorage.instance()
                                .read(inventory.getStackInSlot(0).getTagCompound());
                        for (int i = 0; i < card.stacks.length; i++)
                            this.getFilterStacks().set(i, card.stacks[i]);
                        this.markDirty();
                    }
                }
                break;
            case "PRINT_CARD":
                if (this.getCraftTabProperty().getValue())
                {
                    if (this.getCanPrintProperty().getValue())
                    {
                        final ItemStack punched = new ItemStack(ROSItems.PUNCHED_CARD, 1, 1);
                        punched.setTagCompound(new NBTTagCompound());
                        final CraftCard card = new CraftCard(CardDataStorage.ECardType.CRAFT.getID());
                        for (int i = 0; i < this.getCraftStacks().size(); i++)
                            card.setIngredient(i, this.getCraftStacks().get(i).copy());
                        card.setResult(this.getRecipeResult().copy());
                        CardDataStorage.instance().write(punched.getTagCompound(), card);
                        inventory.extractItem(0, 1, false);

                        if (inventory.getStackInSlot(1).isEmpty())
                            inventory.setStackInSlot(1, punched);
                        else
                            inventory.getStackInSlot(1).grow(1);
                        this.markDirty();
                    }
                }
                else
                {
                    final ItemStack punched = new ItemStack(ROSItems.PUNCHED_CARD, 1, 1);
                    punched.setTagCompound(new NBTTagCompound());
                    final FilterCard card = new FilterCard(CardDataStorage.ECardType.FILTER.getID());
                    for (int i = 0; i < this.getFilterStacks().size(); i++)
                        card.stacks[i] = this.getFilterStacks().get(i).copy();
                    CardDataStorage.instance().write(punched.getTagCompound(), card);
                    inventory.extractItem(0, 1, false);

                    if (inventory.getStackInSlot(1).isEmpty())
                        inventory.setStackInSlot(1, punched);
                    else
                        inventory.getStackInSlot(1).grow(1);
                    this.markDirty();
                }
                break;
            case "MACHINES_LOAD":
                if (this.hasGrid())
                {
                    ClientActionBuilder builder = sender.answer();

                    this.getGridObject().getMachines().forEach((machine, node) ->
                            builder.withLong(machine.name(), node.getBlockPos().toLong()));

                    builder.send();
                }
                break;
            default:
                ROSConstants.LOGGER.warn("Unknown action {} has been sent to TileKeypunch at {}", actionID, this.pos);
                break;
        }
    }

    @Override
    public BlockPos getBlockPos()
    {
        return this.getPos();
    }

    @Override
    public World getBlockWorld()
    {
        return this.world;
    }

    @Override
    public WorkshopMachine getType()
    {
        return WorkshopMachine.KEYPUNCH;
    }

    @Override
    public void onChunkUnload()
    {
        this.disconnectItself();
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (this.isServer() && this.getGrid() == -1)
            TickHandler.loadables.add(this);
        if (this.isClient())
        {
            this.forceSync();
            this.updateState();
        }
    }
}
