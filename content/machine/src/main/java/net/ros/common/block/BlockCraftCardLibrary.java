package net.ros.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.multiblock.BlockMultiblockBase;
import net.ros.common.tile.machine.TileCraftCardLibrary;

public class BlockCraftCardLibrary extends BlockMultiblockBase<TileCraftCardLibrary>
{
    public BlockCraftCardLibrary()
    {
        super("craftcardlibrary", Material.WOOD, TileCraftCardLibrary.class);
    }

    @Override
    public TileCraftCardLibrary getTile(World w, IBlockState state)
    {
        return new TileCraftCardLibrary();
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        if (this.isWordTileCore(w, pos))
            this.getWorldTile(w, pos).disconnectItself();
        super.breakBlock(w, pos, state);
    }
}
