package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.tile.machine.TileKeypunch;

public class BlockKeypunch extends BlockMultiblockBase<TileKeypunch>
{
    public BlockKeypunch()
    {
        super("keypunch", Material.WOOD, TileKeypunch.class);
    }

    @Override
    public TileKeypunch getTile(World w, IBlockState state)
    {
        return new TileKeypunch();
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        if (this.isWordTileCore(w, pos))
            this.getWorldTile(w, pos).disconnectItself();
        super.breakBlock(w, pos, state);
    }
}
