package net.ros.common.grid.impl;

import lombok.Getter;

@Getter
public class HeatPipeGrid extends CableGrid
{
    public HeatPipeGrid(int identifier)
    {
        super(identifier);
    }

    @Override
    public CableGrid copy(int identifier)
    {
        return new HeatPipeGrid(this.getIdentifier());
    }
}
