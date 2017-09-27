package net.qbar.common.recipe.ingredient;

import net.qbar.common.ore.OreStack;

public class OreStackRecipeIngredient extends RecipeIngredient<OreStack>
{
    private OreStack stack;

    public OreStackRecipeIngredient(OreStack stack)
    {
        this.stack = stack;
    }

    @Override
    public boolean match(OreStack against)
    {
        return this.stack.getMineral().equals(against.getMineral());
    }

    @Override
    public boolean matchWithQuantity(OreStack against)
    {
        return this.match(against) && this.stack.getQuantity() <= against.getQuantity();
    }

    @Override
    public int getQuantity()
    {
        return (int) this.stack.getQuantity();
    }

    @Override
    public OreStack getRawIngredient()
    {
        return this.stack;
    }
}
