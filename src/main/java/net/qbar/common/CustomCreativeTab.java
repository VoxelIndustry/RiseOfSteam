package net.qbar.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CustomCreativeTab extends CreativeTabs
{
    public CustomCreativeTab(final String label)
    {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack(Blocks.PISTON, 1, 0);
    }
}
