package net.qbar.common.util;

import net.minecraft.entity.player.InventoryPlayer;
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

    public static String getPrettyStackName(final ItemStack stack)
    {
        return stack.getCount() + " " + stack.getDisplayName();
    }

    public static boolean canMergeStacks(final ItemStack stack1, final ItemStack stack2)
    {
        if (stack1.isEmpty() || stack2.isEmpty())
            return true;
        return stack1.getCount() + stack2.getCount() <= 64 && stack2.getItem() == stack1.getItem()
                && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata())
                && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public static boolean hasPlayerEnough(final InventoryPlayer player, final ItemStack stack, final boolean deepEquals)
    {
        int needed = stack.getCount();
        for (int i = 0; i < player.getSizeInventory(); ++i)
        {
            final ItemStack itemstack = player.getStackInSlot(i);

            if (deepEquals && ItemUtils.deepEquals(stack, itemstack)
                    || !deepEquals && ItemStack.areItemsEqual(stack, itemstack))
            {
                needed -= itemstack.getCount();
                if (needed <= 0)
                    return true;
            }
        }
        return false;
    }

    public static int drainPlayer(final InventoryPlayer player, final ItemStack stack)
    {
        return player.clearMatchingItems(stack.getItem(), stack.getItemDamage(), stack.getCount(),
                stack.getTagCompound());
    }
}
