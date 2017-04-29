package net.qbar.common.recipe.category;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.NonNullList;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;

import java.util.Optional;

public class FurnaceRecipeCategory extends QBarRecipeCategory
{
    public FurnaceRecipeCategory(String name)
    {
        super(name);
    }

    public <T> boolean inputMatchWithoutCount(final int recipeSlot, final T ingredient)
    {
        return !FurnaceRecipes.instance().getSmeltingResult((ItemStack) ingredient).isEmpty();
    }

    public <T> boolean inputMatchWithCount(final int recipeSlot, final T ingredient)
    {
        return !FurnaceRecipes.instance().getSmeltingResult((ItemStack) ingredient).isEmpty();
    }

    public Optional<QBarRecipe> getRecipe(Object... inputs)
    {
        final ItemStack result = FurnaceRecipes.instance().getSmeltingResult((ItemStack) inputs[0]);
        if (!result.isEmpty())
            return Optional.of(new FurnaceRecipeWrapper(((ItemStack) inputs[0]).copy(), result));
        else
            return Optional.empty();
    }

    private static class FurnaceRecipeWrapper extends QBarRecipe
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
}
