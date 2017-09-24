package net.qbar.common.recipe.ingredient;

import net.qbar.common.steam.SteamStack;

public class SteamStackRecipeIngredient extends RecipeIngredient<SteamStack>
{
    private SteamStack ingredient;

    public SteamStackRecipeIngredient(int quantity)
    {
        this.ingredient = new SteamStack(quantity);
    }

    @Override
    public boolean match(SteamStack against)
    {
        return true;
    }

    @Override
    public boolean matchWithQuantity(SteamStack against)
    {
        return this.match(against) && this.getQuantity() <= against.getAmount();
    }

    @Override
    public int getQuantity()
    {
        return this.ingredient.getAmount();
    }

    @Override
    public SteamStack getRawIngredient()
    {
        return this.ingredient;
    }
}
