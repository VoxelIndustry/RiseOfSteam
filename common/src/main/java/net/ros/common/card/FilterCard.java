package net.ros.common.card;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.ros.common.util.ItemUtils;

import java.util.List;

public class FilterCard implements IPunchedCard
{
    private final int ID;
    public ItemStack[] stacks = new ItemStack[9];

    public FilterCard(final int ID)
    {
        this.ID = ID;
        for (int i = 0; i < this.stacks.length; i++)
            this.stacks[i] = ItemStack.EMPTY;
    }

    @Override
    public IPunchedCard readFromNBT(final NBTTagCompound tag)
    {
        final FilterCard card = new FilterCard(CardDataStorage.ECardType.FILTER.getID());

        for (int i = 0; i < card.stacks.length; i++)
            card.stacks[i] = new ItemStack(tag.getCompoundTag("stack_" + i));
        return card;
    }

    @Override
    public void writeToNBT(final NBTTagCompound tag)
    {
        for (int i = 0; i < this.stacks.length; i++)
        {
            final NBTTagCompound stack = new NBTTagCompound();
            this.stacks[i].writeToNBT(stack);
            tag.setTag("stack_" + i, stack);
        }
    }

    @Override
    public void addInformation(final ItemStack stack, final List<String> tooltip, final ITooltipFlag flag)
    {
        tooltip.add("Filter: ");
        for (final ItemStack element : this.stacks)
        {
            if (!element.isEmpty())
                tooltip.add(ItemUtils.getPrettyStackName(element));
        }
    }

    @Override
    public boolean isValid(final NBTTagCompound tag)
    {
        boolean res = true;

        for (int i = 0; i < this.stacks.length && res; i++)
            res = tag.hasKey("stack_" + i);

        return res;
    }

    @Override
    public int getID()
    {
        return this.ID;
    }

    public boolean filter(final ItemStack stack)
    {
        for (final ItemStack filter : this.stacks)
        {
            if (ItemUtils.deepEquals(filter, stack))
                return true;
        }
        return false;
    }
}
