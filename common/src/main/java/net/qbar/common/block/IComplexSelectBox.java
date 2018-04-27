package net.qbar.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public interface IComplexSelectBox
{
    AxisAlignedBB getSelectedBox(EntityPlayer player, BlockPos pos, float partialTicks);
}
