package net.qbar.common.card;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.common.card.PunchedCardDataManager.ECardType;

public class CraftCard implements IPunchedCard
{
    private final int  ID;
    public ItemStack[] recipe = new ItemStack[9];
    public ItemStack   result = ItemStack.EMPTY;

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
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List<String> tooltip,
            final boolean advanced)
    {
        tooltip.add("recipe: ");
        for (final ItemStack element : this.recipe)
        {
            if (element != ItemStack.EMPTY)
                tooltip.add(element.toString());
        }
        tooltip.add("result: " + this.result);

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
