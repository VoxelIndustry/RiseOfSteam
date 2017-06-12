package net.qbar.common.util;

import net.minecraft.util.math.BlockPos;

public class PosUtils
{
	public static final int posDotProd(BlockPos pos1, BlockPos pos2)
	{
			return (pos1.getX()*pos2.getX() + pos1.getY()*pos2.getY() + pos1.getZ()*pos2.getZ());
	}
}
