package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.tile.machine.TileSolarMirror;

public class BlockSolarMirror extends BlockMultiblockBase
{
    public BlockSolarMirror()
    {
        super("solar_mirror", Material.GLASS, Multiblocks.SOLAR_MIRROR);
    }

    @Override
    public TileEntity getTile(final World w, final IBlockState state)
    {
        return new TileSolarMirror();
    }
}
