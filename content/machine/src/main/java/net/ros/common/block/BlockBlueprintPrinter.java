package net.ros.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.multiblock.BlockMultiblockBase;
import net.ros.common.tile.machine.TileBlueprintPrinter;

public class BlockBlueprintPrinter extends BlockMultiblockBase<TileBlueprintPrinter>
{
    public BlockBlueprintPrinter()
    {
        super("blueprintprinter", Material.WOOD, TileBlueprintPrinter.class);
    }

    @Override
    public TileBlueprintPrinter getTile(World w, IBlockState state)
    {
        return new TileBlueprintPrinter();
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        if (this.isWordTileCore(w, pos))
            this.getWorldTile(w, pos).disconnectItself();
        super.breakBlock(w, pos, state);
    }
}
