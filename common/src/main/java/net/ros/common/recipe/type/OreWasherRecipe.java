package net.ros.common.recipe.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.recipe.ingredient.FluidStackRecipeIngredient;
import net.ros.common.recipe.ingredient.ItemStackRandRecipeIngredient;
import net.ros.common.recipe.ingredient.ItemStackRecipeIngredient;
import net.ros.common.recipe.ingredient.RecipeIngredient;
import org.apache.commons.lang3.Range;

public class OreWasherRecipe extends RecipeBase
{
    public OreWasherRecipe(FluidStack inputSludge, FluidStack washer, ItemStack rawOre, ItemStack leftOver)
    {
        NonNullList<RecipeIngredient<?>> ingredients = NonNullList.create();
        ingredients.add(new FluidStackRecipeIngredient(inputSludge));
        ingredients.add(new FluidStackRecipeIngredient(washer));

        this.inputs.put(FluidStack.class, ingredients);

        this.outputs.put(ItemStack.class, NonNullList.create());
        if (!rawOre.isEmpty())
            this.getRecipeOutputs(ItemStack.class).add(new ItemStackRecipeIngredient(rawOre));
        if (!leftOver.isEmpty())
            this.getRecipeOutputs(ItemStack.class).add(new ItemStackRandRecipeIngredient(leftOver,
                    Range.between(0, 1)));
    }

    @Override
    public int getTime()
    {
        return 10;
    }
}
