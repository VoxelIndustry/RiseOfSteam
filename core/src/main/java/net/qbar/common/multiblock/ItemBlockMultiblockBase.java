package net.qbar.common.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockMultiblockBase extends ItemBlock
{
    public ItemBlockMultiblockBase(Block block)
    {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(world, pos))
            pos = pos.offset(facing);

        ItemStack itemstack = player.getHeldItem(hand);
        final BlockMultiblockBase base = (BlockMultiblockBase) this.block;

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack)
                && world.mayPlace(this.block, pos, false, facing, (Entity) null)
                && base.canPlaceBlockAt(world, pos, player.getHorizontalFacing().getOpposite()))
        {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState iblockstate1 = this.block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, i, player,
                    hand);

            if (placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
                SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world,
                        pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS,
                        (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
}
