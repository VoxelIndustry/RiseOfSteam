package net.ros.common.recipe.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.recipe.ingredient.ItemStackRecipeIngredient;

public class SawMillRecipe extends RecipeBase
{
    public SawMillRecipe(final ItemStackRecipeIngredient input, final ItemStackRecipeIngredient output)
    {
        this.inputs.put(ItemStack.class, NonNullList.withSize(1, input));
        this.outputs.put(ItemStack.class, NonNullList.withSize(1, output));
    }

    @Override
    public int getTime()
    {
        return 20;
    }
}
