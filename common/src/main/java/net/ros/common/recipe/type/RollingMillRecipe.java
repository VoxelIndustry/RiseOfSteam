package net.ros.common.recipe.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.recipe.ingredient.ItemStackRecipeIngredient;

public class RollingMillRecipe extends RecipeBase
{
    public RollingMillRecipe(final ItemStackRecipeIngredient metal, final ItemStackRecipeIngredient plate)
    {
        this.inputs.put(ItemStack.class, NonNullList.withSize(1, metal));
        this.outputs.put(ItemStack.class, NonNullList.withSize(1, plate));
    }

    @Override
    public int getTime()
    {
        return 40;
    }
}
