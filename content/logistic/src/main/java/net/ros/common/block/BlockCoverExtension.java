package net.ros.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.block.BlockDirectional.FACING;

public class BlockCoverExtension extends BlockBase
{
    public BlockCoverExtension(String name)
    {
        super(name, Material.IRON);

        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    public EnumFacing getFacing(int meta)
    {
        return EnumFacing.VALUES[meta];
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, this.getFacing(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state)
    {
        Block potentialCore = w.getBlockState(pos.offset(state.getValue(FACING).getOpposite())).getBlock();

        if (potentialCore instanceof BlockPipeBase)
            w.destroyBlock(pos.offset(state.getValue(FACING)), true);
        super.breakBlock(w, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block block, BlockPos from)
    {
        super.neighborChanged(state, w, pos, block, from);

        Block potentialCore = w.getBlockState(pos.offset(state.getValue(FACING).getOpposite())).getBlock();

        if (!(potentialCore instanceof BlockPipeBase))
            w.destroyBlock(pos, false);
    }
}
