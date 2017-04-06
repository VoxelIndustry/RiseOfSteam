package net.qbar.common.multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

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

    AxisAlignedBB getBox(EnumFacing facing);

    Iterable<BlockPos> getAllInBox(BlockPos from, EnumFacing facing);

    MultiblockSide worldSideToMultiblockSide(MultiblockSide side, EnumFacing orientation);
}
