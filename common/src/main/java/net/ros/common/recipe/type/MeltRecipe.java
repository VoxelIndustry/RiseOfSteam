package net.ros.common.recipe.type;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.recipe.ingredient.FluidStackRecipeIngredient;
import net.ros.common.recipe.ingredient.ItemStackRecipeIngredient;

@Getter
public class MeltRecipe extends RecipeBase
{
    private int time;

    private float lowMeltingPoint;
    private float highMeltingPoint;

    public MeltRecipe(ItemStackRecipeIngredient ingredient, FluidStackRecipeIngredient output, float lowMeltingPoint,
                      float highMeltingPoint, int baseMeltingTime)
    {
        this.inputs.put(ItemStack.class, NonNullList.withSize(1, ingredient));
        this.outputs.put(FluidStack.class, NonNullList.withSize(1, output));

        this.lowMeltingPoint = lowMeltingPoint;
        this.highMeltingPoint = highMeltingPoint;
        this.time = baseMeltingTime;
    }

    public FluidStack getOutput()
    {
        return this.getRecipeOutputs(FluidStack.class).get(0).getRaw();
    }
}
