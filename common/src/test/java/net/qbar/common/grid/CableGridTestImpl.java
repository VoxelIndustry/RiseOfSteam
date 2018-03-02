package net.qbar.common.grid;

import net.qbar.common.grid.impl.CableGrid;

public class CableGridTestImpl extends CableGrid
{
    CableGridTestImpl(int identifier)
    {
        super(identifier);
    }

    @Override
    public CableGrid copy(int identifier)
    {
        return new CableGridTestImpl(identifier);
    }
}
