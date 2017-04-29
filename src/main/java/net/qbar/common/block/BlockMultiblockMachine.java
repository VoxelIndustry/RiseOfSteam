package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.IMultiblockDescriptor;

import java.util.function.Supplier;

public class BlockMultiblockMachine extends BlockMultiblockBase
{
    private Supplier<TileEntity> tileSupplier;

    public BlockMultiblockMachine(String name, Material material, IMultiblockDescriptor descriptor,
            Supplier<TileEntity> tileSupplier)
    {
        super(name, material, descriptor);

        this.tileSupplier = tileSupplier;
    }

    @Override
    public TileEntity getTile(World w, IBlockState state)
    {
        return this.tileSupplier.get();
    }
}
