package net.qbar.common.multiblock;

public interface IMultiblockDescriptor
{
    String getName();

    int getWidth();

    int getHeight();

    int getLength();

    int getOffsetX();

    int getOffsetY();

    int getOffsetZ();

    default int getBlockCount()
    {
        return this.getWidth() * this.getHeight() * this.getLength();
    }
}
