package net.qbar.common.recipe.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;

public class SawMillRecipe extends QBarRecipe
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
