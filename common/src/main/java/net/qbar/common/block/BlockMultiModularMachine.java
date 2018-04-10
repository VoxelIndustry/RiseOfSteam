package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.tile.machine.TileModularMachine;

import java.util.function.Supplier;

public class BlockMultiModularMachine<T extends TileModularMachine> extends BlockMultiblockBase<T>
{
    private Supplier<T> tileSupplier;

    public BlockMultiModularMachine(String name, Material material, Supplier<T> tileSupplier, Class<T> tileClass)
    {
        super(name, material, tileClass);

        this.tileSupplier = tileSupplier;
    }

    @Override
    public T getTile(World w, IBlockState state)
    {
        return this.tileSupplier.get();
    }
}