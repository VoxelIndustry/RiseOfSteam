package net.ros.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.ros.common.init.ROSBlocks;

public class CustomCreativeTab extends CreativeTabs
{
    public CustomCreativeTab(final String label)
    {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack(ROSBlocks.BELT);
    }
}
