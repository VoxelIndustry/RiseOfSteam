package net.ros.common.tile.machine;

import com.google.common.collect.Lists;
import fr.ourten.teabeans.value.BaseListProperty;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.ros.common.card.CardDataStorage;
import net.ros.common.card.CardDataStorage.ECardType;
import net.ros.common.card.FilterCard;
import net.ros.common.card.IPunchedCard;
import net.ros.common.grid.node.IBelt;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.util.ItemUtils;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.IActionReceiver;

import java.util.List;

public class TileSplitter extends TileModularMachine implements IContainerProvider, ITickable, IActionReceiver
{
    @Getter
    @Setter
    private EnumFacing facing;

    @Getter
    private       boolean                   isFiltered;
    private final BaseListProperty<Boolean> whitelistProperty;

    private ItemStack cachedWest, cachedNorth, cachedEast;
    private FilterCard filterWest, filterNorth, filterEast;

    public TileSplitter(final boolean isFiltered)
    {
        super(Machines.ITEM_SPLITTER);

        this.isFiltered = isFiltered;

        this.whitelistProperty = new BaseListProperty<>(null, "whitelistProperty");
        this.whitelistProperty.add(true);
        this.whitelistProperty.add(true);
        this.whitelistProperty.add(true);
        this.facing = EnumFacing.UP;

        this.cachedEast = this.cachedNorth = this.cachedWest = ItemStack.EMPTY;
    }

    public TileSplitter()
    {
        this(false);
    }

    private final List<EnumFacing> choices   = Lists.newArrayList();
    private       int              lastSplit = 0;

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 4));
        this.getModule(InventoryModule.class).getInventory("basic")
                .addSlotFilter(3, this::acceptInput);
        this.getModule(InventoryModule.class).getInventory("basic")
                .setSlotLimit(3, 1);

        this.addModule(new IOModule(this));
    }

    @Override
    public void update()
    {
        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");

        if (this.isServer() && !inventory.getStackInSlot(3).isEmpty())
        {
            this.setupFilters(inventory);

            final boolean left = this.hasBelt(this.getFacing().rotateY());
            final boolean front = this.hasBelt(this.getFacing().getOpposite());
            final boolean right = this.hasBelt(this.getFacing().rotateY().getOpposite());

            int split = 0;
            this.choices.clear();

            if (left && this.checkFilter(this.getFacing().rotateY(), inventory.getStackInSlot(3)))
            {
                split++;
                this.choices.add(this.getFacing().rotateY());
            }
            if (front && this.checkFilter(this.getFacing().getOpposite(), inventory.getStackInSlot(3)))
            {
                split++;
                this.choices.add(this.getFacing().getOpposite());
            }
            if (right && this.checkFilter(this.getFacing().rotateY().getOpposite(), inventory.getStackInSlot(3)))
            {
                split++;
                this.choices.add(this.getFacing().rotateYCCW());
            }

            this.lastSplit += 1;
            if (split > 0)
                this.lastSplit %= split;

            for (int i = 0; i < split; i++)
            {
                if (this.canInsert(inventory.getStackInSlot(3), this.choices.get(i == 0 ? this.lastSplit : 0)))
                {
                    this.insert(inventory.getStackInSlot(3), this.choices.get(i == 0 ? this.lastSplit : 0));
                    inventory.setStackInSlot(3, ItemStack.EMPTY);
                    break;
                }
                this.choices.remove(i == 0 ? this.lastSplit : 0);
            }
        }
    }

    private void setupFilters(InventoryHandler inventory)
    {
        if (this.isFiltered())
        {
            if (!ItemUtils.deepEquals(this.cachedWest, inventory.getStackInSlot(0)))
            {
                this.filterWest = null;
                this.cachedWest = inventory.getStackInSlot(0).copy();
                if (this.cachedWest.hasTagCompound())
                {
                    final IPunchedCard card = CardDataStorage.instance()
                            .read(inventory.getStackInSlot(0).getTagCompound());
                    if (card.getID() == ECardType.FILTER.getID())
                        this.filterWest = (FilterCard) card;
                }
            }
            if (!ItemUtils.deepEquals(this.cachedNorth, inventory.getStackInSlot(1)))
            {
                this.filterNorth = null;
                this.cachedNorth = inventory.getStackInSlot(1).copy();
                if (this.cachedNorth.hasTagCompound())
                {
                    final IPunchedCard card = CardDataStorage.instance()
                            .read(inventory.getStackInSlot(1).getTagCompound());
                    if (card.getID() == ECardType.FILTER.getID())
                        this.filterNorth = (FilterCard) card;
                }
            }
            if (!ItemUtils.deepEquals(this.cachedEast, inventory.getStackInSlot(2)))
            {
                this.filterEast = null;
                this.cachedEast = inventory.getStackInSlot(2).copy();
                if (this.cachedEast.hasTagCompound())
                {
                    final IPunchedCard card = CardDataStorage.instance()
                            .read(inventory.getStackInSlot(2).getTagCompound());
                    if (card.getID() == ECardType.FILTER.getID())
                        this.filterEast = (FilterCard) card;
                }
            }
        }
    }

    private boolean checkFilter(final EnumFacing facing, final ItemStack stack)
    {
        if (!this.isFiltered())
            return true;

        FilterCard filter = this.getFilter(facing);
        return filter == null || (!stack.isEmpty() && (this.isWhitelist(facing) == filter.filter(stack)));
    }

    private void insert(final ItemStack stack, final EnumFacing facing)
    {
        ((IBelt) this.world.getTileEntity(this.getPos().down().offset(facing))).insert(stack, true);
    }

    private boolean canInsert(final ItemStack stack, final EnumFacing facing)
    {
        final IBelt belt = (IBelt) this.world.getTileEntity(this.getPos().down().offset(facing));

        return belt.insert(stack, false);
    }

    public boolean hasBelt(final EnumFacing facing)
    {
        final TileEntity tile = this.world.getTileEntity(this.getPos().down().offset(facing));

        return tile instanceof IBelt && ((IBelt) tile).getFacing() == facing;
    }

    private boolean acceptInput(ItemStack stack)
    {
        if (this.isFiltered())
        {
            this.setupFilters(this.getModule(InventoryModule.class).getInventory("basic"));
            return this.checkFilter(this.getFacing().rotateY(), stack) ||
                    this.checkFilter(this.getFacing().rotateYCCW(), stack) ||
                    this.checkFilter(this.getFacing().getOpposite(), stack);
        }
        return (this.hasBelt(this.getFacing().rotateY()) && this.canInsert(stack, this.getFacing().rotateY())) ||
                this.hasBelt(this.getFacing().rotateYCCW()) && this.canInsert(stack, this.getFacing().rotateYCCW())
                || this.hasBelt(this.getFacing().getOpposite()) && this.canInsert(stack, this.getFacing()
                .getOpposite());
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Orientation: " + this.getFacing());
        lines.add("Input: " + this.hasBelt(this.getFacing()));
        lines.add("Output: " + (this.hasBelt(this.getFacing().getOpposite()) ? "FRONT " : "")
                + (this.hasBelt(this.getFacing().rotateY()) ? "LEFT " : "")
                + (this.hasBelt(this.getFacing().rotateYCCW()) ? "RIGHT " : ""));
        lines.add("Buffer: " + this.getModule(InventoryModule.class).getInventory("basic").getStackInSlot(3));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        tag.setInteger("facing", this.facing.ordinal());
        tag.setBoolean("filtered", this.isFiltered);

        for (int i = 0; i < this.whitelistProperty.size(); i++)
            tag.setBoolean("whitelist" + i, this.whitelistProperty.get(i));
        tag.setInteger("whitelistSize", this.whitelistProperty.size());
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
        this.isFiltered = tag.getBoolean("filtered");

        for (int i = 0; i < tag.getInteger("whitelistSize"); i++)
            this.whitelistProperty.set(i, tag.getBoolean("whitelist" + i));
        super.readFromNBT(tag);
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("itemsplitter", player).player(player).inventory(10, 115).hotbar(10, 173)
                .addInventory().tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .filterSlot(0, 25, 92, stack -> stack.getItem() == ROSItems.PUNCHED_CARD)
                .filterSlot(1, 82, 92, stack -> stack.getItem() == ROSItems.PUNCHED_CARD)
                .filterSlot(2, 139, 92, stack -> stack.getItem() == ROSItems.PUNCHED_CARD)
                .syncBooleanValue(() -> this.getWhitelistProperty().get(0),
                        bool -> this.getWhitelistProperty().set(0, bool))
                .syncBooleanValue(() -> this.getWhitelistProperty().get(1),
                        bool -> this.getWhitelistProperty().set(1, bool))
                .syncBooleanValue(() -> this.getWhitelistProperty().get(2),
                        bool -> this.getWhitelistProperty().set(2, bool))
                .addInventory().create();
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.askServerSync();
    }

    public BaseListProperty<Boolean> getWhitelistProperty()
    {
        return this.whitelistProperty;
    }

    private boolean isWhitelist(final EnumFacing facing)
    {
        return this.getWhitelistProperty()
                .get(facing == this.getFacing().getOpposite() ? 1 : facing == this.getFacing().rotateY() ? 0 : 2);
    }

    private void setWhitelist(final EnumFacing facing, final boolean isWhitelist)
    {
        this.getWhitelistProperty().set(
                facing == this.getFacing().getOpposite() ? 1 : facing == this.getFacing().rotateY() ? 0 : 2,
                isWhitelist);
    }

    private FilterCard getFilter(final EnumFacing facing)
    {
        if (facing == this.facing.getOpposite())
            return this.filterNorth;
        if (facing == this.facing.rotateY())
            return this.filterEast;
        if (facing == this.facing.rotateY().getOpposite())
            return this.filterWest;
        return null;
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
