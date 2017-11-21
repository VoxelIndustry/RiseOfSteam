package net.qbar.common.recipe.type;

import lombok.Getter;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.FluidStackRecipeIngredient;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;

public class MeltRecipe extends QBarRecipe
{
    @Getter
    private int time;

    public MeltRecipe(ItemStackRecipeIngredient ingredient, FluidStackRecipeIngredient output, float lowMeltPoint,
                      float highMeltPoint, float baseMeltTime)
    {

    }
}
