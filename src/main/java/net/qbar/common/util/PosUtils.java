package net.qbar.common.util;

import net.minecraft.util.math.BlockPos;

public class PosUtils
{
    public static final int posDotProd(BlockPos pos1, BlockPos pos2)
    {
        return (pos1.getX() * pos2.getX() + pos1.getY() * pos2.getY() + pos1.getZ() * pos2.getZ());
    }

    public static double getHorDistance(BlockPos pos1, BlockPos pos2)
    {
        double xDis = (double) (pos1.getX() - pos2.getX());
        double zDis = (double) (pos1.getZ() - pos2.getZ());
        return Math.sqrt(xDis * xDis + zDis * zDis);
    }
}
