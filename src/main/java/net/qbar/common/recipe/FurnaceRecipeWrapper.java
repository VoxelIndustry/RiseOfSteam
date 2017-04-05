package net.qbar.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;

public class FurnaceRecipeWrapper extends QBarRecipe
{
    public FurnaceRecipeWrapper(final ItemStack ingredient, final ItemStack result)
    {
        this.inputs.put(ItemStack.class, NonNullList.withSize(1, new ItemStackRecipeIngredient(ingredient)));
        this.outputs.put(ItemStack.class, NonNullList.withSize(1, new ItemStackRecipeIngredient(result)));
    }

    @Override
    public int getTime()
    {
        return 200;
    }
}
