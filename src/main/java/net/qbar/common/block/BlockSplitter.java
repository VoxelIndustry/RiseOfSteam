package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.qbar.common.tile.TileSplitter;

public class BlockSplitter extends BlockOrientableMachine
{
    protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 14 / 16D, 1.0D);

    public BlockSplitter()
    {
        super("itemsplitter", Material.IRON, true, false);
    }

    @Override
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos)
    {
        return BlockSplitter.AABB_BOTTOM_HALF;
    }

    @Override
    public boolean isOpaqueCube(final IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(final IBlockState state)
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileSplitter();
    }
}
