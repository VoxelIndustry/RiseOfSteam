package net.qbar.common.grid;

import net.minecraft.item.ItemStack;

public interface IBeltInput
{
    ItemStack[] inputItems();

    boolean canInput(IBelt into);
}
