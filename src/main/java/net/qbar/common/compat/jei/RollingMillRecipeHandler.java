package net.qbar.common.compat.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.recipe.RollingMillRecipe;

public class RollingMillRecipeHandler implements IRecipeHandler<RollingMillRecipe>
{
    private final IJeiHelpers jeiHelpers;

    public RollingMillRecipeHandler(final IJeiHelpers jeiHelpers)
    {
        this.jeiHelpers = jeiHelpers;
    }

    @Override
    public Class<RollingMillRecipe> getRecipeClass()
    {
        return RollingMillRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(final RollingMillRecipe recipe)
    {
        return QBarRecipeHandler.ROLLINGMILL_UID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(final RollingMillRecipe recipe)
    {
        return new RollingMillRecipeWrapper(this.jeiHelpers, recipe);
    }

    @Override
    public boolean isRecipeValid(final RollingMillRecipe recipe)
    {
        return true;
    }
}