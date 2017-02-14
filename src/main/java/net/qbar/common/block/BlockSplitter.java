package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.tile.TileSplitter;

public class BlockSplitter extends BlockOrientableMachine
{
    public BlockSplitter()
    {
        super("itemsplitter", Material.IRON, true, false);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileSplitter();
    }
}
