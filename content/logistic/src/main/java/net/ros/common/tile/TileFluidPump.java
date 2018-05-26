package net.ros.common.tile;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.block.BlockOrientableMachine;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;

import java.util.List;

public class TileFluidPump extends TileFluidPipe
{
    public TileFluidPump(PipeType type, int transferCapacity)
    {
        super(type, transferCapacity);
    }

    public TileFluidPump()
    {
        this(null, 0);
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
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getGridObject().getTank());
        return super.getCapability(capability, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        super.addInfo(lines);
        lines.add("Transfer Rate: " + this.transferCapacity + " mB / tick");
        lines.add("Orientation: " + this.getFacing());
    }

    @Override
    public void drainNeighbors()
    {
        for (IFluidHandler fluidHandler : this.adjacentHandler.values())
        {
            if (fluidHandler != null)
            {
                int simulated = this.getGridObject().getTank().fill(
                        fluidHandler.drain(this.transferCapacity, false), false);

                if (simulated > 0)
                    this.getGridObject().getTank().fill(fluidHandler.drain(simulated, true), true);
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
        return false;
    }
}
