package net.qbar.common.tile.machine;

import fr.ourten.teabeans.value.BaseProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.qbar.client.render.tile.VisibilityModelState;
import net.qbar.common.card.CardDataStorage;
import net.qbar.common.card.CardDataStorage.ECardType;
import net.qbar.common.card.FilterCard;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.grid.IBelt;
import net.qbar.common.init.QBarItems;
import net.qbar.common.network.action.ActionSender;
import net.qbar.common.network.action.IActionReceiver;
import net.qbar.common.tile.TileInventoryBase;
import net.qbar.common.util.ItemUtils;

import java.util.List;

public class TileExtractor extends TileInventoryBase implements IContainerProvider, ITickable, IActionReceiver
{
    private EnumFacing facing;

    private       boolean               hasFilter;
    private final BaseProperty<Boolean> whitelistProperty;

    private FilterCard filter;
    private ItemStack cached = ItemStack.EMPTY;

    public TileExtractor(final boolean hasFilter)
    {
        super("itemextractor", 1);

        this.facing = EnumFacing.UP;

        this.hasFilter = hasFilter;

        this.whitelistProperty = new BaseProperty<>(true, "whitelistProperty");
    }

    public TileExtractor()
    {
        this(false);
    }

    @Override
    public void update()
    {
        if (this.hasFilter() && !ItemUtils.deepEquals(this.cached, this.getStackInSlot(0)))
        {
            final boolean had = this.filter == null;
            this.filter = null;
            this.cached = this.getStackInSlot(0).copy();
            if (this.cached.hasTagCompound())
            {
                final IPunchedCard card = CardDataStorage.instance()
                        .read(this.getStackInSlot(0).getTagCompound());
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
    public void addInfo(final List<String> lines)
    {
        lines.add("Orientation: " + this.getFacing());
        lines.add("Inventory: " + this.hasItemHandler());
        lines.add("Belt: " + this.hasBelt());
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
        return new ContainerBuilder("itemextractor", player).player(player.inventory).inventory(8, 95).hotbar(8, 153)
                .addInventory().tile(this)
                .filterSlot(0, 80, 73, stack -> !stack.isEmpty() && stack.getItem().equals(QBarItems.PUNCHED_CARD))
                .syncBooleanValue(this.getWhitelistProperty()::getValue, this.getWhitelistProperty()::setValue)
                .addInventory().create();
    }

    public void setFacing(final EnumFacing facing)
    {
        this.facing = facing;
    }

    public EnumFacing getFacing()
    {
        return this.facing;
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }

    public boolean hasFilter()
    {
        return this.hasFilter;
    }

    public void setHasFilter(final boolean hasFilter)
    {
        this.hasFilter = hasFilter;
    }

    public BaseProperty<Boolean> getWhitelistProperty()
    {
        return this.whitelistProperty;
    }

    public boolean isWhitelist(final EnumFacing facing)
    {
        return this.getWhitelistProperty().getValue();
    }

    public void setWhitelist(final EnumFacing facing, final boolean isWhitelist)
    {
        this.getWhitelistProperty().setValue(isWhitelist);
    }

    public FilterCard getFilter(final EnumFacing facing)
    {
        return this.filter;
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
