package net.ros.common.grid.node;

import net.ros.common.grid.impl.PipeGrid;

public interface IFluidPipe extends ITileCable<PipeGrid>
{
    void fillNeighbors();
}
