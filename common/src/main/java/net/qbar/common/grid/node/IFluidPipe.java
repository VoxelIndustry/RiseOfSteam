package net.qbar.common.grid.node;

import net.qbar.common.grid.impl.PipeGrid;
import net.qbar.common.grid.node.ITileCable;

public interface IFluidPipe extends ITileCable<PipeGrid>
{
    void fillNeighbors();
}
