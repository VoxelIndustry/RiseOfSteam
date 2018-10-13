package net.ros.common.card;

import lombok.Getter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.ros.common.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilterCard implements IPunchedCard
{
    private final int             ID;
    @Getter
    private       ItemStack[]     filters          = new ItemStack[9];
    @Getter
    private       List<ItemStack> compressedFilter = new ArrayList<>();

    public FilterCard(final int ID)
    {
        this.ID = ID;
        for (int i = 0; i < this.filters.length; i++)
            this.filters[i] = ItemStack.EMPTY;
    }

    public void setFilter(int slot, ItemStack filter)
    {
        if (!filter.isEmpty())
        {
            Optional<ItemStack> existing = compressedFilter.stream()
                    .filter(stack -> ItemUtils.deepEquals(filter, stack)).findAny();

            if (existing.isPresent())
                existing.get().grow(filter.getCount());
            else
                this.compressedFilter.add(filter.copy());
        }

        this.filters[slot] = filter;
    }

    @Override
    public IPunchedCard readFromNBT(NBTTagCompound tag)
    {
        final FilterCard card = new FilterCard(CardDataStorage.ECardType.FILTER.getID());

        for (int i = 0; i < card.filters.length; i++)
            card.setFilter(i, new ItemStack(tag.getCompoundTag("stack_" + i)));
        return card;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        for (int i = 0; i < this.filters.length; i++)
        {
            final NBTTagCompound stack = new NBTTagCompound();
            this.filters[i].writeToNBT(stack);
            tag.setTag("stack_" + i, stack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, List<String> tooltip, ITooltipFlag flag)
    {
        tooltip.add("Filter: ");

        if (!flag.isAdvanced())
            return;
        for (ItemStack element : this.compressedFilter)
            tooltip.add(ItemUtils.getPrettyStackName(element));
    }

    @Override
    public boolean isValid(NBTTagCompound tag)
    {
        boolean res = true;

        for (int i = 0; i < this.filters.length && res; i++)
            res = tag.hasKey("stack_" + i);

        return res;
    }

    @Override
    public int getID()
    {
        return this.ID;
    }


    public boolean filter(ItemStack stack)
    {
        for (final ItemStack filter : this.filters)
        {
            if (ItemUtils.deepEquals(filter, stack))
                return true;
        }
        return false;
    }
}
