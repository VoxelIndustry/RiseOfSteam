package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.ros.common.block.BlockFluidValve;
import net.ros.common.grid.GridManager;
import net.ros.common.grid.node.ITileNode;

import javax.annotation.Nullable;
import java.util.List;

public class TileFluidValve extends TileFluidPipe
{
    private static FluidTank EMPTY_TANK = new FluidTank(0);

    public TileFluidValve(int transferCapacity)
    {
        super(transferCapacity);
    }

    public TileFluidValve()
    {
        this(0);
    }

    @Getter
    private boolean isOpen;

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.isOpen = tag.getBoolean("isOpen");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setBoolean("isOpen", this.isOpen);

        return super.writeToNBT(tag);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        super.addInfo(lines);

        lines.add("Axis: " + this.getAxis());
        lines.add("Facing: " + this.getFacing());
        lines.add("Open: " + this.isOpen());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == this.capability)
            return facing.getAxis() == this.getAxis();
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == this.capability && facing.getAxis() == this.getAxis())
        {
            if (this.isOpen())
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getGridObject().getTank());
            else
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(EMPTY_TANK);
        }
        return super.getCapability(capability, facing);
    }

    private EnumFacing.Axis getAxis()
    {
        return this.world.getBlockState(this.pos).getValue(BlockFluidValve.AXIS);
    }

    private EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockFluidValve.FACING);
    }

    public void setOpen(boolean isOpen)
    {
        boolean previous = this.isOpen;
        this.isOpen = isOpen;

        if (previous != isOpen)
        {
            if (isOpen)
                GridManager.getInstance().connectCable(this);
            else
            {
                this.disconnectItself();

                for (EnumFacing facing : EnumFacing.VALUES)
                {
                    if (!this.isConnected(facing))
                        continue;

                    this.disconnect(facing);
                }
                this.setGrid(-1);
                GridManager.getInstance().connectCable(this);
            }
        }
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        if (this.getAxis() == EnumFacing.Axis.X && to.getBlockPos().getZ() != this.getPos().getZ())
            return false;
        if (this.getAxis() == EnumFacing.Axis.Z && to.getBlockPos().getX() != this.getPos().getX())
            return false;
        if (this.getAxis() == EnumFacing.Axis.Y && to.getBlockPos().getY() != this.getPos().getY())
            return false;

        return this.isOpen && super.canConnect(facing, to);
    }
}
