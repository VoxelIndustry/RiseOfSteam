package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.qbar.common.grid.IBelt;
import net.qbar.common.tile.TileExtractor;

public class BlockExtractor extends BlockMachineBase
{
    public static PropertyDirection      FACING           = PropertyDirection.create("facing", facing -> true);

    protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB AABB_TOP_HALF    = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_NORTH_HALF  = new AxisAlignedBB(0D, 0D, 0.5D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_SOUTH_HALF  = new AxisAlignedBB(0D, 0D, 0D, 1.0D, 1.0D, 0.5D);
    protected static final AxisAlignedBB AABB_EAST_HALF   = new AxisAlignedBB(0D, 0D, 0D, 0.5D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_WEST_HALF   = new AxisAlignedBB(0.5D, 0D, 0D, 1.0D, 1.0D, 1.0D);

    public BlockExtractor()
    {
        super("itemextractor", Material.IRON);

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockExtractor.FACING, EnumFacing.UP));
    }

    @Override
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos)
    {
        switch (state.getValue(BlockExtractor.FACING))
        {
            case DOWN:
                return BlockExtractor.AABB_TOP_HALF;
            case EAST:
                return BlockExtractor.AABB_EAST_HALF;
            case NORTH:
                return BlockExtractor.AABB_NORTH_HALF;
            case SOUTH:
                return BlockExtractor.AABB_SOUTH_HALF;
            case WEST:
                return BlockExtractor.AABB_WEST_HALF;
            default:
                return BlockExtractor.AABB_BOTTOM_HALF;
        }
    }

    @Override
    public boolean isFullyOpaque(final IBlockState state)
    {
        return false;
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
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockExtractor.FACING);
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        final int facingInt = state.getValue(BlockExtractor.FACING).ordinal();
        return facingInt;
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        int facingInt = meta;
        if (facingInt > 4)
            facingInt = facingInt - 4;
        final EnumFacing facing = EnumFacing.VALUES[facingInt];
        return this.getDefaultState().withProperty(BlockExtractor.FACING, facing);
    }

    public EnumFacing getFacing(final IBlockState state)
    {
        return state.getValue(BlockExtractor.FACING);
    }

    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
    {
        return this.getStateFromMeta(meta).withProperty(BlockExtractor.FACING, facing);
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing facing)
    {
        if (facing == null)
            return false;
        world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockExtractor.FACING, facing));
        if (!world.isRemote)
            ((TileExtractor) world.getTileEntity(pos)).setFacing(facing);
        return true;
    }

    @Override
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
            final EntityLivingBase placer, final ItemStack stack)
    {
        super.onBlockPlacedBy(w, pos, state, placer, stack);

        if (!w.isRemote)
            ((TileExtractor) w.getTileEntity(pos)).setFacing(state.getValue(BlockExtractor.FACING));
    }

    @Override
    public void neighborChanged(final IBlockState state, final World w, final BlockPos pos, final Block block,
            final BlockPos posNeighbor)
    {
        if (pos.offset(state.getValue(BlockExtractor.FACING)).equals(posNeighbor))
        {
            if (w.getTileEntity(posNeighbor) != null && w.getTileEntity(posNeighbor) instanceof IBelt
                    && ((IBelt) w.getTileEntity(posNeighbor)).getFacing() == state.getValue(BlockExtractor.FACING))
                ((IBelt) w.getTileEntity(posNeighbor)).connectInput(pos);
        }
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileExtractor();
    }
}
