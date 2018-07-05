package net.ros.common.tile;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ITileInfoList
{
    void addText(String text);

    void addProgress(int current, int max);

    void addItem(ItemStack stack);

    void addFluid(FluidStack stack, int capacity);

    void addEntity(Entity e);
}
