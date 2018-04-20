package net.qbar.common.recipe.ingredient;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackRecipeIngredient extends RecipeIngredient<FluidStack>
{
    private FluidStack ingredient;

    public FluidStackRecipeIngredient(FluidStack ingredient)
    {
        this.ingredient = ingredient;
    }

    public FluidStackRecipeIngredient(Fluid ingredient)
    {
        this(new FluidStack(ingredient, 1));
    }

    @Override
    public boolean match(FluidStack against)
    {
        return against.getFluid().equals(this.getRaw().getFluid())
                && FluidStack.areFluidStackTagsEqual(against, this.getRaw());
    }

    @Override
    public boolean matchWithQuantity(FluidStack against)
    {
        return this.match(against) && this.getQuantity() <= against.amount;
    }

    @Override
    public int getQuantity()
    {
        return this.ingredient.amount;
    }

    @Override
    public FluidStack getRaw()
    {
        return this.ingredient;
    }
}
