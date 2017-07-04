package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.tile.machine.TileSolarMirror;

public class BlockSolarMirror extends BlockMultiblockBase<TileSolarMirror>
{
    public BlockSolarMirror()
    {
        super("solar_mirror", Material.GLASS, Multiblocks.SOLAR_MIRROR, TileSolarMirror.class);
    }

    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos)
    {
        if (this.checkWorldTile(world, pos))
        {
            final TileSolarMirror tile = this.getWorldTile(world, pos);
            return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, tile.state);
        }
        return state;
    }

    @Override
    public TileSolarMirror getTile(final World w, final IBlockState state)
    {
        return new TileSolarMirror();
    }
}
