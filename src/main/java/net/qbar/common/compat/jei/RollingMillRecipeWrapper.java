package net.qbar.common.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.qbar.client.gui.GuiRollingMill;
import net.qbar.common.recipe.RollingMillRecipe;

import java.util.stream.Collectors;

public class RollingMillRecipeWrapper extends BlankRecipeWrapper
{
    private final IDrawableAnimated arrow;

    private final RollingMillRecipe recipe;

    public RollingMillRecipeWrapper(final IJeiHelpers jeiHelpers, final RollingMillRecipe recipe)
    {
        this.recipe = recipe;

        final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        final IDrawableStatic arrowStatic = guiHelper.createDrawable(GuiRollingMill.BACKGROUND, 176, 14, 24, 17);
        this.arrow = guiHelper.createAnimatedDrawable(arrowStatic, 20, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void drawInfo(final Minecraft minecraft, final int recipeWidth, final int recipeHeight, final int mouseX,
                         final int mouseY)
    {
        super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
        this.arrow.draw(minecraft, 33, 19);
    }

    @Override
    public void getIngredients(final IIngredients ingredients)
    {
        ingredients.setInputs(ItemStack.class, this.recipe.getRecipeInputs(ItemStack.class).stream()
                .map(ingredient -> ingredient.getRawIngredient()).collect(Collectors.toList()));
        ingredients.setOutputs(ItemStack.class, this.recipe.getRecipeOutputs(ItemStack.class).stream()
                .map(ingredient -> ingredient.getRawIngredient()).collect(Collectors.toList()));
    }
}
