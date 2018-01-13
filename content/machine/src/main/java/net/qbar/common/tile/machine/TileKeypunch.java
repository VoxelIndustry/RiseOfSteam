package net.qbar.common.tile.machine;

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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.CraftCard;
import net.qbar.common.card.FilterCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.EmptyContainer;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.ITileWorkshop;
import net.qbar.common.grid.WorkshopMachine;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.network.action.ActionSender;
import net.qbar.common.network.action.IActionReceiver;
import net.qbar.common.tile.TileMultiblockInventoryBase;

import java.util.List;

public class TileKeypunch extends TileMultiblockInventoryBase implements IActionReceiver, ITileWorkshop
{
    private final int[] INPUT  = new int[]{0};
    private final int[] OUTPUT = new int[]{1};

    private final BaseListProperty<ItemStack> craftStacks;
    private final BaseListProperty<ItemStack> filterStacks;

    private final BaseProperty<Boolean> isCraftTabProperty, canPrintProperty;

    private final InventoryCrafting fakeInv = new InventoryCrafting(new EmptyContainer(), 3, 3);

    @Getter
    @Setter
    private       int grid;
    @Getter
    private final LinkedListMultimap<BlockPos, ITileWorkshop> connectionsMap = LinkedListMultimap.create();

    public TileKeypunch()
    {
        super("keypunch", 2);

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
        return new ContainerBuilder("keypunch", player).player(player.inventory).inventory(8, 93).hotbar(8, 151)
                .addInventory().tile(this)
                .filterSlot(0, 26, 70, stack -> !stack.isEmpty() && stack.getItem().equals(QBarItems.PUNCHED_CARD))
                .outputSlot(1, 134, 70)
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

    @Override
    public int[] getSlotsForFace(final EnumFacing side)
    {
        return side.equals(EnumFacing.DOWN) ? this.OUTPUT : this.INPUT;
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack stack, final EnumFacing side)
    {
        if (!side.equals(EnumFacing.DOWN))
            return this.isItemValidForSlot(index, stack);
        return false;
    }

    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing side)
    {
        return side.equals(EnumFacing.DOWN);
    }

    public BaseProperty<Boolean> getCraftTabProperty()
    {
        return this.isCraftTabProperty;
    }

    public BaseProperty<Boolean> getCanPrintProperty()
    {
        return this.canPrintProperty;
    }

    public BaseListProperty<ItemStack> getCraftStacks()
    {
        return this.craftStacks;
    }

    public BaseListProperty<ItemStack> getFilterStacks()
    {
        return this.filterStacks;
    }

    public ItemStack getRecipeResult()
    {
        for (int i = 0; i < 9; i++)
            this.fakeInv.setInventorySlotContents(i, this.getCraftStacks().get(i));

        IRecipe recipe = CraftingManager.findMatchingRecipe(this.fakeInv, this.getWorld());
        return recipe != null ? recipe.getRecipeOutput() : ItemStack.EMPTY;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) this.getInventoryWrapper(facing);
        return null;
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.KEYPUNCH.getUniqueID(), this.getWorld(), this.pos.getX()
                , this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
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
                    if (this.getStackInSlot(0).hasTagCompound() && this.getStackInSlot(0).getTagCompound()
                            .getInteger("cardTypeID") == PunchedCardDataManager.ECardType.CRAFT.getID())
                    {
                        final CraftCard card = (CraftCard) PunchedCardDataManager.getInstance()
                                .readFromNBT(this.getStackInSlot(0).getTagCompound());
                        for (int i = 0; i < card.recipe.length; i++)
                            this.getCraftStacks().set(i, card.recipe[i]);
                        this.markDirty();

                    }
                }
                else
                {
                    if (this.getStackInSlot(0).hasTagCompound() && this.getStackInSlot(0).getTagCompound()
                            .getInteger("cardTypeID") == PunchedCardDataManager.ECardType.FILTER.getID())
                    {
                        final FilterCard card = (FilterCard) PunchedCardDataManager.getInstance()
                                .readFromNBT(this.getStackInSlot(0).getTagCompound());
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
                        final ItemStack punched = new ItemStack(QBarItems.PUNCHED_CARD, 1, 1);
                        punched.setTagCompound(new NBTTagCompound());
                        final CraftCard card = new CraftCard(PunchedCardDataManager.ECardType.CRAFT.getID());
                        for (int i = 0; i < this.getCraftStacks().size(); i++)
                            card.recipe[i] = this.getCraftStacks().get(i);
                        card.result = this.getRecipeResult();
                        PunchedCardDataManager.getInstance().writeToNBT(punched.getTagCompound(), card);
                        this.decrStackSize(0, 1);
                        this.setInventorySlotContents(1, punched);
                        this.markDirty();
                    }
                }
                else
                {
                    final ItemStack punched = new ItemStack(QBarItems.PUNCHED_CARD, 1, 1);
                    punched.setTagCompound(new NBTTagCompound());
                    final FilterCard card = new FilterCard(PunchedCardDataManager.ECardType.FILTER.getID());
                    for (int i = 0; i < this.getFilterStacks().size(); i++)
                        card.stacks[i] = this.getFilterStacks().get(i);
                    PunchedCardDataManager.getInstance().writeToNBT(punched.getTagCompound(), card);
                    this.decrStackSize(0, 1);
                    this.setInventorySlotContents(1, punched);
                    this.markDirty();
                }
                break;
            default:
                QBarConstants.LOGGER.warn("Unknown action {} has been sent to TileKeypunch at {}", actionID, this.pos);
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
