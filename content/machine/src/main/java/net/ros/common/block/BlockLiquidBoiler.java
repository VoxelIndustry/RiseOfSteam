package net.ros.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import net.ros.common.tile.machine.TileLiquidBoiler;

public class BlockLiquidBoiler extends BlockMultiModularMachine<TileLiquidBoiler>
{
    public BlockLiquidBoiler()
    {
        super("liquidfuel_boiler", Material.IRON, TileLiquidBoiler::new, TileLiquidBoiler.class);
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos)
    {
        if (this.checkWorldTile(world, pos))
        {
            final TileLiquidBoiler tile = this.getWorldTile(world, pos);
            return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, tile.state);
        }
        return state;
    }
}
