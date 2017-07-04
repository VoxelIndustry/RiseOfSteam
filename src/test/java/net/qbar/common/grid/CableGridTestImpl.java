package net.qbar.common.grid;

public class CableGridTestImpl extends CableGrid
{
    public CableGridTestImpl(int identifier)
    {
        super(identifier);
    }

    @Override
    CableGrid copy(int identifier)
    {
        return new CableGridTestImpl(identifier);
    }
}
