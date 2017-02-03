package net.qbar.common.multiblock;

public class MultiblockDescriptorBase implements IMultiblockDescriptor
{
    private final String name;

    private final int    width, height, length, offsetX, offsetY, offsetZ;

    MultiblockDescriptorBase(final String name, final int width, final int height, final int length, final int offsetX,
            final int offsetY, final int offsetZ)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.length = length;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.height;
    }

    @Override
    public int getLength()
    {
        return this.length;
    }

    @Override
    public int getOffsetX()
    {
        return this.offsetX;
    }

    @Override
    public int getOffsetY()
    {
        return this.offsetY;
    }

    @Override
    public int getOffsetZ()
    {
        return this.offsetZ;
    }
}
