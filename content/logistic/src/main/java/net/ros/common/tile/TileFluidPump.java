package net.ros.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.fluid.LimitedTank;

import java.util.List;

public class TileFluidPump extends TileBase implements ITickable
{
    private int           transferCapacity;
    private IFluidHandler top;
    private IFluidHandler bottom;

    private EnumFacing facing;

    private final LimitedTank tank;

    public TileFluidPump(final int transferCapacity)
    {
        this.transferCapacity = transferCapacity;
        this.tank = new LimitedTank(0, 0);
        this.tank.setCanDrain(false);
        this.tank.setCanFill(false);
        this.facing = EnumFacing.UP;
    }

    public TileFluidPump()
    {
        this(16);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if ((this.facing.equals(facing) || this.facing.getOpposite().equals(facing))
                && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if ((this.facing.equals(facing) || this.facing.getOpposite().equals(facing))
                && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) this.tank;
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        tag.setInteger("facing", this.facing.ordinal());
        tag.setInteger("transferCapacity", this.transferCapacity);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
        this.transferCapacity = tag.getInteger("transferCapacity");

        super.readFromNBT(tag);
    }

    @Override
    public void update()
    {
        if (!this.world.isRemote)
        {
            if (this.world.getTotalWorldTime() % 40 == 0)
                this.scanFluidHandlers();
            if (this.top != null && this.bottom != null)
                this.transferFluids();
        }
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Connect: " + (this.top != null ? "top " : "") + (this.bottom != null ? "bottom" : ""));
        lines.add("Transfer Rate: " + this.transferCapacity + " mB / tick");
        lines.add("Orientation: " + this.getFacing());
    }

    public void transferFluids()
    {
        final FluidStack transfered = this.bottom.drain(this.transferCapacity, false);
        int qty = 0;

        if (transfered != null && transfered.amount > 0)
        {
            qty = this.top.fill(transfered, false);
            if (qty > 0)
                this.top.fill(this.bottom.drain(this.transferCapacity, true), true);
        }
    }

    public void scanFluidHandlers()
    {
        for (final EnumFacing facing : EnumFacing.VALUES)
            this.scanFluidHandler(this.getPos().offset(facing), facing);
    }

    public void scanFluidHandler(final BlockPos posNeighbor, final EnumFacing facing)
    {
        if (facing == this.getFacing() || facing == this.getFacing().getOpposite())
        {
            final TileEntity tile = this.world.getTileEntity(posNeighbor);

            if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
            {
                if (facing == this.getFacing())
                    this.top = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
                else
                    this.bottom = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
            }
            else if (facing == this.getFacing() && this.top != null)
                this.top = null;
            else if (facing == this.getFacing().getOpposite() && this.bottom != null)
                this.bottom = null;
        }
    }

    public int getTransferCapacity()
    {
        return this.transferCapacity;
    }

    public EnumFacing getFacing()
    {
        return this.facing;
    }

    public void setFacing(final EnumFacing facing)
    {
        this.facing = facing;
    }
}
