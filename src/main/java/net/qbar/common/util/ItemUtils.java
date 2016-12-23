package net.qbar.common.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemUtils
{
    public static boolean deepEquals(final ItemStack a, final ItemStack b)
    {
        if (a == ItemStack.EMPTY || b == ItemStack.EMPTY || a.getItem() != b.getItem()
                || !ItemStack.areItemStackTagsEqual(a, b))
            return false;
        if (a.getHasSubtypes())
        {
            if (ItemUtils.isWildcard(a.getItemDamage()) || ItemUtils.isWildcard(b.getItemDamage()))
                return true;
            if (a.getItemDamage() != b.getItemDamage())
                return false;
        }
        return true;
    }

    public static boolean isWildcard(final int damage)
    {
        return damage == -1 || damage == OreDictionary.WILDCARD_VALUE;
    }
}
