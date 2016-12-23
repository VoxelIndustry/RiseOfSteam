package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.tile.TileTank;

public class BlockTank extends BlockMachineBase
{
    public BlockTank()
    {
        super("tank", Material.IRON);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileTank();
    }
}
