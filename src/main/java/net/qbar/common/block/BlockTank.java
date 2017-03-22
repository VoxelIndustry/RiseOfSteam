package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.IMultiblockDescriptor;
import net.qbar.common.tile.machine.TileTank;

public class BlockTank extends BlockMultiblockBase
{
    private final int capacity;

    public BlockTank(final String name, final IMultiblockDescriptor descriptor, final int capacity)
    {
        super(name, Material.IRON, descriptor);

        this.capacity = capacity;
    }

    @Override
    public TileEntity getTile(final World w, final IBlockState state)
    {
        return new TileTank(this.capacity);
    }
}
