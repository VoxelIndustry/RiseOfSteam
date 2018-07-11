package net.ros.common.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface RightClickAction
{
    RightClickAction EMPTY = (w, pos, state, player, hand, side, hitX, hitY, hitZ) -> false;

    boolean apply(World w, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
                  float hitX, float hitY, float hitZ);
}
