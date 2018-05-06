package net.ros.common.grid;

import net.ros.common.grid.impl.CableGrid;

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
