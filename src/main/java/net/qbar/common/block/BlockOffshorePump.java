package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.tile.TileOffshorePump;

public class BlockOffshorePump extends BlockMachineBase
{
    public BlockOffshorePump()
    {
        super("offshore_pump", Material.IRON);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileOffshorePump(64);
    }
}
