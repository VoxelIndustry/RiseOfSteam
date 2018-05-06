package net.ros.common.recipe.type;

import lombok.Getter;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.recipe.ingredient.FluidStackRecipeIngredient;
import net.ros.common.recipe.ingredient.SteamStackRecipeIngredient;
import net.ros.common.steam.SteamStack;

public class LiquidBoilerRecipe extends RecipeBase
{
    @Getter
    private int time;

    public LiquidBoilerRecipe(Fluid fluid, int heatPerMb, int timePerBucket)
    {
        this.inputs.put(FluidStack.class, NonNullList.withSize(1, new FluidStackRecipeIngredient(fluid)));
        this.outputs.put(SteamStack.class, NonNullList.withSize(1, new SteamStackRecipeIngredient(heatPerMb)));

        this.time = timePerBucket;
    }
}
