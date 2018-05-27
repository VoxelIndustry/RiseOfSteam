package net.ros.common.compat.jei;

import lombok.Getter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.ros.common.ROSConstants;
import net.ros.common.recipe.RecipeHandler;

import java.util.ArrayList;

@Getter
public class JEIBlueprintRecipeCategory implements IRecipeCategory<BlueprintRecipeWrapper>
{
    private final IDrawable background;
    private final String    title = I18n.format("gui.blueprint.name");
    private final String    uid   = RecipeHandler.BLUEPRINT_UID;

    public JEIBlueprintRecipeCategory(IGuiHelper guiHelper)
    {
        this.background = guiHelper.createBlankDrawable(160, 120);
    }

    @Override
    public String getModName()
    {
        return ROSConstants.MODNAME;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BlueprintRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 0, 0);
        guiItemStacks.set(0, recipeWrapper.getBlueprint().getRodStack());

        int index = 1;
        int y = 0;
        for (ArrayList<ItemStack> step : recipeWrapper.getBlueprint().getSteps())
        {
            int x = 0;
            for (ItemStack stack : step)
            {
                guiItemStacks.init(index, true, x * 18, 22 + y * 20);
                guiItemStacks.set(index, stack);
                x++;
                index++;
            }
            y++;
        }
    }
}
