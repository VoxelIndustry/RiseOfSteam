package net.qbar.common.card.type;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.common.card.IPunchedCard;

public class FilterCard implements IPunchedCard
{
    public ItemStack[] stacks = new ItemStack[9];

    public FilterCard()
    {
        for (int i = 0; i < stacks.length; i++)
        {
            stacks[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        for (int i = 0; i < stacks.length; i++)
        {
            NBTTagCompound stack = (NBTTagCompound) tag.getTag("stack_" + i);
            stacks[i] = new ItemStack(stack);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        for (int i = 0; i < stacks.length; i++)
        {
            NBTTagCompound stack = new NBTTagCompound();
            stacks[i].writeToNBT(stack);
            tag.setTag("stack_" + i, stack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add("elements: ");
        for (int i = 0; i < stacks.length; i++)
        {
            if (stacks[i] != ItemStack.EMPTY)
                tooltip.add(stacks[i].toString());
        }
    }

    @Override
    public boolean isValid(NBTTagCompound tag)
    {
        boolean res = true;

        for (int i = 0; i < stacks.length && res; i++)
            res = tag.hasKey("stack_" + i);

        return res;
    }

}
