package net.ros.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class CustomCreativeTab extends CreativeTabs
{
    private Supplier<Block> iconSupplier;

    public CustomCreativeTab(final String label, Supplier<Block> iconSupplier)
    {
        super(label);

        this.iconSupplier = iconSupplier;
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack(iconSupplier.get());
    }
}
