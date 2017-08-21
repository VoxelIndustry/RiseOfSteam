package net.qbar.common.compat.jei;

import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.qbar.client.gui.GuiRollingMill;
import net.qbar.common.recipe.type.RollingMillRecipe;
import net.qbar.common.recipe.ingredient.RecipeIngredient;

import java.util.stream.Collectors;

public class RollingMillRecipeWrapper implements IRecipeWrapper
{
    private final IDrawableAnimated arrow;

    private final RollingMillRecipe recipe;

    RollingMillRecipeWrapper(final RollingMillRecipe recipe)
    {
        this.recipe = recipe;

        final IDrawableStatic arrowStatic = QBarJEIPlugin.instance().getGuiHelper()
                .createDrawable(GuiRollingMill.BACKGROUND, 176, 14, 24, 17);
        this.arrow = QBarJEIPlugin.instance().getGuiHelper()
                .createAnimatedDrawable(arrowStatic, 20, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void drawInfo(final Minecraft minecraft, final int recipeWidth, final int recipeHeight, final int mouseX,
                         final int mouseY)
    {
        this.arrow.draw(minecraft, 33, 19);
    }

    @Override
    public void getIngredients(final IIngredients ingredients)
    {
        ingredients.setInputs(ItemStack.class, this.recipe.getRecipeInputs(ItemStack.class).stream()
                .map(RecipeIngredient::getRawIngredient).collect(Collectors.toList()));
        ingredients.setOutputs(ItemStack.class, this.recipe.getRecipeOutputs(ItemStack.class).stream()
                .map(RecipeIngredient::getRawIngredient).collect(Collectors.toList()));
    }
}
