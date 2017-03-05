package net.qbar.common.tile;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import fr.ourten.teabeans.value.BaseProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.qbar.common.card.FilterCard;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.grid.IBelt;
import net.qbar.common.grid.IBeltInput;

public class TileSplitter extends TileInventoryBase
        implements ITileInfoProvider, IContainerProvider, IBeltInput, ITickable, IFilteredMachine, ISidedInventory
{
    private EnumFacing                  facing;

    private boolean                     hasFilter;
    private final BaseProperty<Boolean> whitelistProperty;

    private ItemStack                   cacheWest, cacheNorth;

    private final ItemStack             cacheEast;
    private FilterCard                  filterWest, filterNorth, filterEast;

    public TileSplitter(final boolean hasFilter)
    {
        super("itemsplitter", 4);

        this.hasFilter = hasFilter;

        this.whitelistProperty = new BaseProperty<>(true, "whitelistProperty");
        this.facing = EnumFacing.UP;

        this.cacheEast = this.cacheNorth = this.cacheWest = ItemStack.EMPTY;
    }

    public TileSplitter()
    {
        this(false);
    }

    private final List<EnumFacing> choices   = Lists.newArrayList();
    private int                    lastSplit = 0;

    @Override
    public void update()
    {
        if (this.isServer() && !this.getStackInSlot(3).isEmpty())
        {
            final boolean left = this.hasBelt(this.getFacing().rotateY());
            final boolean front = this.hasBelt(this.getFacing().getOpposite());
            final boolean right = this.hasBelt(this.getFacing().rotateY().getOpposite());

            int split = 0;
            this.choices.clear();

            if (left)
            {
                split++;
                this.choices.add(this.getFacing().rotateY());
            }
            if (front)
            {
                split++;
                this.choices.add(this.getFacing().getOpposite());
            }
            if (right)
            {
                split++;
                this.choices.add(this.getFacing().rotateY().getOpposite());
            }

            this.lastSplit += 1;
            this.lastSplit %= split;

            for (int i = 0; i < split; i++)
            {
                if (this.canInsert(this.getStackInSlot(3), this.choices.get(i == 0 ? this.lastSplit : 0)))
                {
                    this.insert(this.getStackInSlot(3), this.choices.get(i == 0 ? this.lastSplit : 0));
                    this.setInventorySlotContents(3, ItemStack.EMPTY);
                    break;
                }
                this.choices.remove(i == 0 ? this.lastSplit : 0);
            }
        }
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

    private boolean hasBelt(final EnumFacing facing)
    {
        final TileEntity tile = this.world.getTileEntity(this.getPos().down().offset(facing));

        return tile != null && tile instanceof IBelt;
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Orientation: " + this.getFacing());
        lines.add("Input: " + this.hasBelt(this.getFacing()));
        lines.add("Output: " + (this.hasBelt(this.getFacing().getOpposite()) ? "FRONT " : "")
                + (this.hasBelt(this.getFacing().rotateY()) ? "LEFT " : "")
                + (this.hasBelt(this.getFacing().rotateY().getOpposite()) ? "RIGHT " : ""));
        lines.add("Buffer: " + this.getStackInSlot(3));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        tag.setInteger("facing", this.facing.ordinal());
        tag.setBoolean("filtered", this.hasFilter);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
        this.hasFilter = tag.getBoolean("filtered");

        super.readFromNBT(tag);
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
    public ItemStack[] inputItems()
    {
        return null;
    }

    @Override
    public boolean canInput(final IBelt into)
    {
        return false;
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return null;
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

    @Override
    public boolean isWhitelist()
    {
        return this.getWhitelistProperty().getValue();
    }

    @Override
    public void setWhitelist(final boolean isWhitelist)
    {
        this.getWhitelistProperty().setValue(isWhitelist);
    }

    @Override
    public FilterCard getFilter(final EnumFacing facing)
    {
        if (facing == this.facing)
            return this.filterNorth;
        if (facing == this.facing.rotateY())
            return this.filterEast;
        if (facing == this.facing.rotateY().getOpposite())
            return this.filterWest;
        return null;
    }

    private final int[] inputSlot = new int[] { 3 };

    @Override
    public int[] getSlotsForFace(final EnumFacing side)
    {
        return this.inputSlot;
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction)
    {
        if (index == 3)
            return this.isItemValidForSlot(index, itemStackIn);
        return false;
    }

    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction)
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    IItemHandler inventoryHandler = new SidedInvWrapper(this, EnumFacing.NORTH);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, @Nullable final EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == this.getFacing())
            return (T) this.inventoryHandler;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, @Nullable final EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == this.getFacing())
            return true;
        return super.hasCapability(capability, facing);
    }
}
