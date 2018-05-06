package net.ros.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.IWrenchable;

public class ItemWrench extends ItemBase
{
    public ItemWrench()
    {
        super("wrench");
    }

    @Override
    public EnumActionResult onItemUse(final EntityPlayer player, final World world, final BlockPos pos,
                                      final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ)
    {
        final Block block = world.getBlockState(pos).getBlock();
        if (block instanceof IWrenchable)
        {
            ((IWrenchable) block).onWrench(player, world, pos, hand, facing, world.getBlockState(pos),
                    player.getActiveItemStack());
            return EnumActionResult.SUCCESS;
        }
        else if (block.rotateBlock(world, pos, facing.rotateAround(facing.getAxis())))
            return EnumActionResult.SUCCESS;
        return EnumActionResult.PASS;
    }
}
