package net.qbar.common.recipe.type;

import lombok.Getter;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.FluidStackRecipeIngredient;
import net.qbar.common.recipe.ingredient.RecipeIngredient;

@Getter
public class AlloyRecipe extends QBarRecipe
{
    private FluidStackRecipeIngredient biggestStack;

    public AlloyRecipe(FluidStackRecipeIngredient firstIngredient, FluidStackRecipeIngredient secondIngredient,
                       FluidStackRecipeIngredient output)
    {
        NonNullList<RecipeIngredient<?>> ingredients = NonNullList.create();
        ingredients.add(firstIngredient);
        ingredients.add(secondIngredient);

        if (firstIngredient.getQuantity() > secondIngredient.getQuantity())
            biggestStack = firstIngredient;
        else
            biggestStack = secondIngredient;

        this.inputs.put(FluidStack.class, ingredients);
        this.outputs.put(FluidStack.class, NonNullList.withSize(1, output));
    }

    public FluidStack getOutput()
    {
        return this.getRecipeOutputs(FluidStack.class).get(0).getRaw();
    }

    public NonNullList<RecipeIngredient<FluidStack>> getInputs()
    {
        return this.getRecipeInputs(FluidStack.class);
    }

    @Override
    public int getTime()
    {
        return 0;
    }
}
