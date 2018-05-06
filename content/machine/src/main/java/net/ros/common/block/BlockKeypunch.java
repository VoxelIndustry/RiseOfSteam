package net.ros.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.tile.machine.TileKeypunch;

public class BlockKeypunch extends BlockMultiModularMachine<TileKeypunch>
{
    public BlockKeypunch()
    {
        super("keypunch", Material.WOOD, TileKeypunch::new, TileKeypunch.class);
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        if (this.isWordTileCore(w, pos))
            this.getWorldTile(w, pos).disconnectItself();
        super.breakBlock(w, pos, state);
    }
}
