package net.qbar.common.card;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.common.card.PunchedCardDataManager.ECardType;

public class FilterCard implements IPunchedCard
{
    private final int  ID;
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
        final FilterCard card = new FilterCard(ECardType.FILTER.getID());

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
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List<String> tooltip,
            final boolean advanced)
    {
        tooltip.add("Filter: ");
        for (final ItemStack stack2 : this.stacks)
        {
            if (!stack2.isEmpty())
                tooltip.add(stack2.toString());
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
}
