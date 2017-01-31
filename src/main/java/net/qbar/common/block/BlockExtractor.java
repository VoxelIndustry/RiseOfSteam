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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.grid.IBelt;
import net.qbar.common.tile.TileExtractor;

public class BlockExtractor extends BlockMachineBase
{
    public static PropertyDirection FACING = PropertyDirection.create("facing", facing -> true);

    public BlockExtractor()
    {
        super("itemextractor", Material.IRON);

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockExtractor.FACING, EnumFacing.UP));
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

        w.setBlockState(pos,
                state.withProperty(BlockExtractor.FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);
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
