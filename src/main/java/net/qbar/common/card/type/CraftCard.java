package net.qbar.common.card.type;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.common.card.IPunchedCard;

public class CraftCard implements IPunchedCard
{
    public ItemStack[] recipe = new ItemStack[9];
    public ItemStack   result = ItemStack.EMPTY;

    public CraftCard()
    {
        for (int i = 0; i < recipe.length; i++)
        {
            recipe[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        result = new ItemStack((NBTTagCompound) tag.getTag("stack_result"));

        for (int i = 0; i < recipe.length; i++)
        {
            recipe[i] = new ItemStack((NBTTagCompound) tag.getTag("stack_" + i));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        NBTTagCompound res = new NBTTagCompound();
        result.writeToNBT(res);
        tag.setTag("stack_result", res);

        for (int i = 0; i < recipe.length; i++)
        {
            NBTTagCompound stack = new NBTTagCompound();
            recipe[i].writeToNBT(stack);
            tag.setTag("stack_" + i, stack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add("recipe: ");
        for (int i = 0; i < recipe.length; i++)
        {
            if (recipe[i] != ItemStack.EMPTY)
                tooltip.add(recipe[i].toString());
        }
        tooltip.add("result: " + this.result);

    }

    @Override
    public boolean isValid(NBTTagCompound tag)
    {
        boolean res = tag.hasKey("stack_result");

        for (int i = 0; i < recipe.length && res; i++)
            res = tag.hasKey("stack_" + i);

        return res;
    }

}
