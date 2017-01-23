package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.grid.GridManager;
import net.qbar.common.tile.TileFluidPipe;

public class BlockFluidPipe extends BlockMachineBase
{
    public BlockFluidPipe()
    {
        super("fluidpipe", Material.IRON);
    }

    @Override
    public void neighborChanged(final IBlockState state, final World w, final BlockPos pos, final Block block,
            final BlockPos posNeighbor)
    {
        if (!w.isRemote)
            ((TileFluidPipe) w.getTileEntity(pos)).scanFluidHandlers(posNeighbor);
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        GridManager.getInstance().disconnectCable((TileFluidPipe) w.getTileEntity(pos));

        super.breakBlock(w, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileFluidPipe(64);
    }
}
