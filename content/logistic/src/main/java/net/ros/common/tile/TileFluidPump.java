package net.ros.common.tile;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.block.BlockOrientableMachine;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;

import java.util.List;
import java.util.Map;

public class TileFluidPump extends TileFluidPipe
{
    public TileFluidPump(PipeType type)
    {
        super(type);
    }

    public TileFluidPump()
    {
        this(null);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if ((this.getFacing().equals(facing) || this.getFacing().getOpposite().equals(facing))
                && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if ((this.getFacing().equals(facing) || this.getFacing().getOpposite().equals(facing))
                && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getBufferTank());
        return super.getCapability(capability, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        super.addInfo(lines);
        lines.add("Transfer Rate: " + this.getTransferRate() + " mB / tick");
        lines.add("Orientation: " + this.getFacing());
    }

    @Override
    public void fillNeighbors()
    {
        if (this.tileEntityInvalid)
            return;

        for (Map.Entry<EnumFacing, IFluidHandler> entry : this.adjacentHandler.entrySet())
        {
            if (entry.getKey() == this.getFacing().getOpposite())
                continue;

            if (this.getBufferTank().getFluidAmount() != 0 && entry.getValue() != null)
            {
                int simulated = entry.getValue().fill(this.getBufferTank().drain(this.getTransferRate(), false), false);

                if (simulated > 0)
                    entry.getValue().fill(this.getBufferTank().drain(simulated, true), true);
            }
        }
    }

    @Override
    public void drainNeighbors()
    {
        if (this.tileEntityInvalid)
            return;

        for (Map.Entry<EnumFacing, IFluidHandler> entry : this.adjacentHandler.entrySet())
        {
            if (entry.getKey() != this.getFacing().getOpposite())
                continue;

            if (entry.getValue() != null)
            {
                int simulated = this.getBufferTank().fill(entry.getValue().drain(this.getTransferRate(), false), false);

                if (simulated > 0)
                    this.getBufferTank().fill(entry.getValue().drain(simulated, true), true);
            }
        }
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(pos).getValue(BlockOrientableMachine.FACING);
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        if (facing == this.getFacing() || facing == this.getFacing().getOpposite())
            return super.canConnect(facing, to);
        return false;
    }

    @Override
    public boolean isInput()
    {
        return true;
    }

    @Override
    public boolean isOutput()
    {
        return true;
    }
}
