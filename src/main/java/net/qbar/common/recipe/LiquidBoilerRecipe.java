package net.qbar.common.recipe;

import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.qbar.common.recipe.ingredient.FluidStackRecipeIngredient;
import net.qbar.common.recipe.ingredient.SteamStackRecipeIngredient;

public class LiquidBoilerRecipe extends QBarRecipe
{
    private int time;

    public LiquidBoilerRecipe(Fluid fluid, int steamPerMb, int timePerBucket)
    {
        this.inputs.put(FluidStack.class, NonNullList.withSize(1, new FluidStackRecipeIngredient(fluid)));
        this.outputs.put(Integer.class, NonNullList.withSize(1, new SteamStackRecipeIngredient(steamPerMb)));

        this.time = timePerBucket;
    }

    @Override
    public int getTime()
    {
        return this.time;
    }
}
