package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.tile.machine.TileTank;

public class BlockTank extends BlockMultiblockBase<TileTank>
{
    private final int capacity;
    private final int tier;

    public BlockTank(final String name, final int capacity, final int tier)
    {
        super(name, Material.IRON, TileTank.class);

        this.capacity = capacity;
        this.tier = tier;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos)
    {
        if (this.checkWorldTile(world, pos))
        {
            final TileTank tile = this.getWorldTile(world, pos);
            if (tile.getTier() != 0)
                return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, tile.state);
        }
        return state;
    }

    @Override
    public TileTank getTile(final World w, final IBlockState state)
    {
        return new TileTank(this.capacity, this.tier);
    }
}
