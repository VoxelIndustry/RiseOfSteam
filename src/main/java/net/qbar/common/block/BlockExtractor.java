package net.qbar.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.qbar.common.grid.IBelt;
import net.qbar.common.tile.TileExtractor;

public class BlockExtractor extends BlockMachineBase
{
    public static PropertyBool           FILTER           = PropertyBool.create("filter");
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
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockExtractor.FACING, EnumFacing.UP)
                .withProperty(BlockExtractor.FILTER, false));
    }

    @Override
    public void getSubBlocks(final Item item, final CreativeTabs tab, final NonNullList<ItemStack> stacks)
    {
        stacks.add(new ItemStack(item, 1, 0));
        stacks.add(new ItemStack(item, 1, 1));
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
        return new BlockStateContainer(this, BlockExtractor.FACING, BlockExtractor.FILTER);
    }

    @Override
    public int damageDropped(final IBlockState state)
    {
        return state.getValue(BlockExtractor.FILTER) ? 1 : 0;
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(BlockExtractor.FACING).getIndex();

        if (state.getValue(BlockExtractor.FILTER).booleanValue())
            i |= 8;
        return i;
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(BlockExtractor.FACING, BlockExtractor.getFacing(meta))
                .withProperty(BlockExtractor.FILTER, Boolean.valueOf((meta & 8) > 0));
    }

    @Nullable
    public static EnumFacing getFacing(final int meta)
    {
        final int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public EnumFacing getFacing(final IBlockState state)
    {
        return state.getValue(BlockExtractor.FACING);
    }

    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
    {
        return this.getStateFromMeta(meta).withProperty(BlockExtractor.FACING, facing)
                .withProperty(BlockExtractor.FILTER, meta == 1);
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
        return new TileExtractor(this.getStateFromMeta(meta).getValue(BlockExtractor.FILTER));
    }
}
