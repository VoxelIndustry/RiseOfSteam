package net.qbar.common.grid;

public class BeltGrid extends CableGrid
{
    public BeltGrid(final int identifier)
    {
        super(identifier);
    }

    @Override
    CableGrid copy(final int identifier)
    {
        return new BeltGrid(identifier);
    }
}
