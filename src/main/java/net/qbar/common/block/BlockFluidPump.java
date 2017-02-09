package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.tile.TileFluidPump;

public class BlockFluidPump extends BlockOrientableMachine
{
    public BlockFluidPump()
    {
        super("fluidpump", Material.IRON, true, true);
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
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
            final EntityLivingBase placer, final ItemStack stack)
    {
        super.onBlockPlacedBy(w, pos, state, placer, stack);
        if (!w.isRemote)
        {
            ((TileFluidPump) w.getTileEntity(pos)).setFacing(this.getFacing(state));
            ((TileFluidPump) w.getTileEntity(pos)).scanFluidHandlers();
        }
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing facing)
    {
        super.rotateBlock(world, pos, facing);

        if (!world.isRemote)
        {
            ((TileFluidPump) world.getTileEntity(pos)).setFacing(facing);
            ((TileFluidPump) world.getTileEntity(pos)).scanFluidHandlers();
        }
        return true;
    }

    @Override
    public void neighborChanged(final IBlockState state, final World w, final BlockPos pos, final Block block,
            final BlockPos posNeighbor)
    {
        if (!w.isRemote)
        {
            final BlockPos substract = posNeighbor.subtract(pos);
            ((TileFluidPump) w.getTileEntity(pos)).scanFluidHandler(posNeighbor,
                    EnumFacing.getFacingFromVector(substract.getX(), substract.getY(), substract.getZ()));
        }
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileFluidPump(64);
    }
}
