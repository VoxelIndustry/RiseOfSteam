package net.qbar.common.machine.module.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.InventoryModule;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

public class BasicInventoryModule extends InventoryModule
{
    public BasicInventoryModule(IModularMachine machine)
    {
        super(machine, "BasicInventoryModule");
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(@Nonnull EnumFacing side)
    {
        return IntStream.range(0, this.getStacks().size()).toArray();
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing side)
    {
        return this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing side)
    {
        return true;
    }
}
