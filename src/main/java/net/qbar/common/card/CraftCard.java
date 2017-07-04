package net.qbar.common.card;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.common.card.PunchedCardDataManager.ECardType;
import net.qbar.common.util.ItemUtils;

import java.util.List;

public class CraftCard implements IPunchedCard
{
    private final int ID;
    public ItemStack[] recipe = new ItemStack[9];
    private ItemStack[] cachedCombine;
    public ItemStack result = ItemStack.EMPTY;

    public CraftCard(final int ID)
    {
        this.ID = ID;
        for (int i = 0; i < this.recipe.length; i++)
            this.recipe[i] = ItemStack.EMPTY;
    }

    @Override
    public IPunchedCard readFromNBT(final NBTTagCompound tag)
    {
        final CraftCard card = new CraftCard(ECardType.CRAFT.getID());

        card.result = new ItemStack((NBTTagCompound) tag.getTag("stack_result"));

        for (int i = 0; i < card.recipe.length; i++)
            card.recipe[i] = new ItemStack((NBTTagCompound) tag.getTag("stack_" + i));
        return card;
    }

    @Override
    public void writeToNBT(final NBTTagCompound tag)
    {
        final NBTTagCompound res = new NBTTagCompound();
        this.result.writeToNBT(res);
        tag.setTag("stack_result", res);

        for (int i = 0; i < this.recipe.length; i++)
        {
            final NBTTagCompound stack = new NBTTagCompound();
            this.recipe[i].writeToNBT(stack);
            tag.setTag("stack_" + i, stack);
        }
    }

    @Override
    public void addInformation(final ItemStack stack, final List<String> tooltip, final ITooltipFlag flag)
    {
        tooltip.add("Recipe: ");
        for (final ItemStack element : this.recipe)
        {
            if (!element.isEmpty())
                tooltip.add(ItemUtils.getPrettyStackName(element));
        }
        tooltip.add("");
        tooltip.add(" -> " + ItemUtils.getPrettyStackName(this.result));

    }

    @Override
    public boolean isValid(final NBTTagCompound tag)
    {
        boolean res = tag.hasKey("stack_result");

        for (int i = 0; i < this.recipe.length && res; i++)
            res = tag.hasKey("stack_" + i);

        return res;
    }

    @Override
    public int getID()
    {
        return this.ID;
    }
}
