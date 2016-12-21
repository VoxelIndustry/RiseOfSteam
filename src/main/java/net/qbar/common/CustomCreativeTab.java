package net.qbar.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * @author Ourten 21 déc. 2016
 */
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
