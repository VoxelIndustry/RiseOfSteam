package net.qbar.common.grid;

public class BeltGrid extends CableGrid
{
    private final float beltSpeed;

    public BeltGrid(final int identifier, final float beltSpeed)
    {
        super(identifier);

        this.beltSpeed = beltSpeed;
    }

    @Override
    CableGrid copy(final int identifier)
    {
        return new BeltGrid(identifier, this.beltSpeed);
    }
}
