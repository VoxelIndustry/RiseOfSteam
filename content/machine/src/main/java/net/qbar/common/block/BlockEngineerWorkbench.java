package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.tile.machine.TileEngineerWorkbench;

public class BlockEngineerWorkbench extends BlockMultiblockBase<TileEngineerWorkbench>
{
    public BlockEngineerWorkbench()
    {
        super("engineerworkbench", Material.WOOD, TileEngineerWorkbench.class);
    }

    @Override
    public TileEngineerWorkbench getTile(World w, IBlockState state)
    {
        return new TileEngineerWorkbench();
    }
}
