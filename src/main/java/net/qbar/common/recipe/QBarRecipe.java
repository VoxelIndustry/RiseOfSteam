package net.qbar.common.recipe;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public abstract class QBarRecipe
{
    public abstract NonNullList<ItemStack> getRecipeInputs();

    public abstract NonNullList<ItemStack> getRecipeOutputs();

    public Optional<NonNullList<ItemStack>> getRemainingItems()
    {
        return Optional.empty();
    }
}
