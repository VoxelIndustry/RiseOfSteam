package net.qbar.common.grid;

import javax.annotation.Nullable;

public interface IFluidPipe extends ITileCable
{
    void fillNeighbors();

    @Nullable
    public default PipeGrid getGridObject()
    {
        final CableGrid grid = GridManager.getInstance().getGrid(this.getGrid());

        if (grid != null && grid instanceof PipeGrid)
            return (PipeGrid) grid;
        return null;
    }
}
