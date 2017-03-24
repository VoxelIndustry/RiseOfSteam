package net.qbar.common.recipe;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.qbar.common.util.ItemUtils;

public abstract class QBarRecipe
{
    public abstract NonNullList<ItemStack> getRecipeInputs();

    public abstract NonNullList<ItemStack> getRecipeOutputs();

    public Optional<NonNullList<ItemStack>> getRemainingItems()
    {
        return Optional.empty();
    }

    public abstract int getTime();

    public boolean match(final ItemStack... inputs)
    {
        int i = 0;
        for (final ItemStack stack : inputs)
        {
            if (i >= this.getRecipeInputs().size())
                return false;
            if (!ItemUtils.deepEquals(stack, this.getRecipeInputs().get(i)))
                return false;
            i++;
        }
        return true;
    }
}
