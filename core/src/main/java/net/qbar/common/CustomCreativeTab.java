package net.qbar.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.qbar.common.init.QBarBlocks;

public class CustomCreativeTab extends CreativeTabs
{
    public CustomCreativeTab(final String label)
    {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack(QBarBlocks.BELT);
    }
}
