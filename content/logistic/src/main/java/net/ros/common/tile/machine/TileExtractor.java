package net.ros.common.tile.machine;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.ros.client.render.tile.VisibilityModelState;
import net.ros.common.card.CardDataStorage;
import net.ros.common.card.CardDataStorage.ECardType;
import net.ros.common.card.FilterCard;
import net.ros.common.card.IPunchedCard;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.grid.node.IBelt;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.IActionReceiver;
import net.ros.common.tile.ITileInfoList;
import net.ros.common.util.ItemUtils;

public class TileExtractor extends TileModularMachine implements IContainerProvider, ITickable, IActionReceiver
{
    @Getter
    @Setter
    private EnumFacing facing;

    @Setter
    private       boolean               hasFilter;
    @Getter
    private final BaseProperty<Boolean> whitelistProperty;

    @Getter
    private FilterCard filter;
    private ItemStack  cached = ItemStack.EMPTY;

    public TileExtractor(final boolean hasFilter)
    {
        super(Machines.ITEM_EXTRACTOR);

        this.facing = EnumFacing.UP;

        this.hasFilter = hasFilter;
        this.whitelistProperty = new BaseProperty<>(true, "whitelistProperty");
    }

    public TileExtractor()
    {
        this(false);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 1));
    }

    @Override
    public void update()
    {
        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");

        if (this.hasFilter() && !ItemUtils.deepEquals(this.cached, inventory.getStackInSlot(0)))
        {
            final boolean had = this.filter == null;
            this.filter = null;
            this.cached = inventory.getStackInSlot(0).copy();
            if (this.cached.hasTagCompound())
            {
                final IPunchedCard card = CardDataStorage.instance()
                        .read(inventory.getStackInSlot(0).getTagCompound());
                if (card.getID() == ECardType.FILTER.getID())
                    this.filter = (FilterCard) card;
            }

            if (this.isClient() && had != (this.filter == null))
                this.updateState();
        }

        if (this.hasItemHandler() && this.hasBelt())
        {
            final IItemHandler itemHandler = this.getItemHandler();

            final int slots = itemHandler.getSlots();
            int currentSlot = -1;
            ItemStack simulated = ItemStack.EMPTY;
            for (int i = 0; i < slots; i++)
            {
                simulated = itemHandler.extractItem(i, 1, true);
                if (!simulated.isEmpty() && this.applyFilter(simulated))
                {
                    currentSlot = i;
                    break;
                }
            }

            if (currentSlot != -1 && this.canInsert(simulated) && this.useSteam(1, false))
            {
                this.insert(itemHandler.extractItem(currentSlot, 1, false));
                this.useSteam(1, true);
            }
        }
    }

    private boolean applyFilter(ItemStack stack)
    {
        if (!this.hasFilter() || this.filter == null)
            return true;
        if (this.getWhitelistProperty().getValue() && this.filter.filter(stack) ||
                !this.getWhitelistProperty().getValue() && !this.filter.filter(stack))
            return true;
        return false;
    }

    private boolean useSteam(final int amount, final boolean use)
    {
        if (((IBelt) this.world.getTileEntity(this.getPos().down())).getGridObject() != null)
            return ((IBelt) this.world.getTileEntity(this.getPos().down())).getGridObject().getTank().drainSteam(amount,
                    use) == amount;
        return false;
    }

    private void insert(final ItemStack stack)
    {
        ((IBelt) this.world.getTileEntity(this.getPos().down())).insert(stack, true);
    }

    private boolean canInsert(final ItemStack stack)
    {
        final IBelt belt = (IBelt) this.world.getTileEntity(this.getPos().down());

        return belt.insert(stack, false);
    }

    private boolean hasBelt()
    {
        final TileEntity tile = this.world.getTileEntity(this.getPos().down());

        return tile != null && tile instanceof IBelt;
    }

    private IItemHandler getItemHandler()
    {
        return this.world.getTileEntity(this.getPos().offset(this.getFacing().getOpposite()))
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.getFacing());
    }

    private boolean hasItemHandler()
    {
        final TileEntity tile = this.world.getTileEntity(this.getPos().offset(this.getFacing().getOpposite()));

        return tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.getFacing());
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        list.addText("Orientation: " + this.getFacing());
        list.addText("Inventory: " + this.hasItemHandler());
        list.addText("Belt: " + this.hasBelt());
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        tag.setInteger("facing", this.facing.ordinal());
        tag.setBoolean("filtered", this.hasFilter);

        tag.setBoolean("whitelist", this.whitelistProperty.getValue());
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        final EnumFacing previous = this.facing;
        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
        this.hasFilter = tag.getBoolean("filtered");

        this.whitelistProperty.setValue(tag.getBoolean("whitelist"));
        super.readFromNBT(tag);

        if (this.isClient() && previous != this.facing)
            this.updateState();
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("itemextractor", player).player(player).inventory(8, 107).hotbar(8, 165)
                .addInventory().tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .filterSlot(0, 80, 85, stack -> stack.getItem().equals(ROSItems.PUNCHED_CARD))
                .syncBooleanValue(this.getWhitelistProperty()::getValue, this.getWhitelistProperty()::setValue)
                .addInventory().create();
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.askServerSync();
    }

    public boolean isWhitelist(final EnumFacing facing)
    {
        return this.getWhitelistProperty().getValue();
    }

    public void setWhitelist(final EnumFacing facing, final boolean isWhitelist)
    {
        this.getWhitelistProperty().setValue(isWhitelist);
    }

    public boolean hasFilter()
    {
        return this.hasFilter;
    }

    ////////////
    // RENDER //
    ////////////

    public final VisibilityModelState state = new VisibilityModelState();

    private void updateState()
    {
        if (this.isServer())
        {
            this.sync();
            return;
        }
        this.state.parts.clear();

        if (this.getFacing().getAxis().isHorizontal())
            this.state.parts.add("legs");

        if (this.hasFilter())
        {
            if (this.filter == null)
                this.state.parts.add("card");
        }

        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("WHITELIST".equals(actionID))
        {
            this.setWhitelist(EnumFacing.values()[payload.getInteger("facing")],
                    payload.getBoolean("whitelist"));
            this.markDirty();
        }
    }
}
