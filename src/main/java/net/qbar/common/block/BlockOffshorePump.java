package net.qbar.common.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.tile.TileOffshorePump;

public class BlockOffshorePump extends BlockMachineBase
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockOffshorePump()
    {
        super("offshore_pump", Material.IRON);

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockOffshorePump.FACING, EnumFacing.NORTH));
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
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(BlockOffshorePump.FACING,
                placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
            final EntityLivingBase placer, final ItemStack stack)
    {
        w.setBlockState(pos, state.withProperty(BlockOffshorePump.FACING, placer.getHorizontalFacing().getOpposite()),
                2);
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
            enumfacing = EnumFacing.NORTH;

        return this.getDefaultState().withProperty(BlockOffshorePump.FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(BlockOffshorePump.FACING).getIndex();
    }

    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot)
    {
        return state.withProperty(BlockOffshorePump.FACING, rot.rotate(state.getValue(BlockOffshorePump.FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockOffshorePump.FACING);
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing facing)
    {
        if (facing == null || !EnumFacing.Plane.HORIZONTAL.apply(facing))
            return false;
        world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockOffshorePump.FACING, facing));
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileOffshorePump(64);
    }
}
