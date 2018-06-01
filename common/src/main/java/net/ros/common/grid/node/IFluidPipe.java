package net.ros.common.grid.node;

import net.minecraftforge.fluids.FluidTank;
import net.ros.common.grid.impl.PipeGrid;

public interface IFluidPipe extends IPipe<PipeGrid>
{
    default void fillNeighbors()
    {

    }

    default void drainNeighbors()
    {

    }

    boolean isInput();

    boolean isOutput();

    FluidTank getBufferTank();

    int getTransferRate();
}
