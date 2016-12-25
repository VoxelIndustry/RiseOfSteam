package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.tile.TileBoiler;

public class BlockBoiler extends BlockMachineBase
{
    public BlockBoiler()
    {
        super("boiler", Material.IRON);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileBoiler();
    }
}
