package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.tile.TileSolarMirror;

public class BlockSolarMirror extends BlockMachineBase
{
    public BlockSolarMirror()
    {
        super("solar_mirror", Material.GLASS);
    }

    @Override
    public boolean isOpaqueCube(final IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(final IBlockState state)
    {
        return false;
    }

    @Override
    public boolean causesSuffocation(final IBlockState state)
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileSolarMirror();
    }
}
