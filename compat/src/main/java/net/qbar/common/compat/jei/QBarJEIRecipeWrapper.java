package net.qbar.common.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.RecipeIngredient;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class QBarJEIRecipeWrapper implements IRecipeWrapper
{
    private final IDrawableAnimated arrow;

    private final QBarRecipe recipe;

    QBarJEIRecipeWrapper(final QBarRecipe recipe, IGuiHelper guiHelper, ResourceLocation background, int u, int v, int width, int height)
    {
        this.recipe = recipe;

        final IDrawableStatic arrowStatic = guiHelper.createDrawable(background, u, v, width, height);
        this.arrow = guiHelper.createAnimatedDrawable(arrowStatic, 20, IDrawableAnimated.StartDirection.LEFT, false);
    }

    public static Builder builder(IGuiHelper guiHelper)
    {
        return new Builder(guiHelper);
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
                .map(RecipeIngredient::getRaw).collect(Collectors.toList()));
        ingredients.setOutputs(ItemStack.class, this.recipe.getRecipeOutputs(ItemStack.class).stream()
                .map(RecipeIngredient::getRaw).collect(Collectors.toList()));
    }

    public static class Builder
    {
        private ResourceLocation background;
        private int              u, v, width, height;
        private final IGuiHelper guiHelper;

        public Builder(IGuiHelper guiHelper)
        {
            this.guiHelper = guiHelper;
        }

        public Builder background(ResourceLocation background)
        {
            this.background = background;
            return this;
        }

        public Builder u(int u)
        {
            this.u = u;
            return this;
        }

        public Builder v(int v)
        {
            this.v = v;
            return this;
        }

        public Builder width(int width)
        {
            this.width = width;
            return this;
        }

        public Builder height(int height)
        {
            this.height = height;
            return this;
        }

        @Nonnull
        public QBarJEIRecipeWrapper create(QBarRecipe recipe)
        {
            return new QBarJEIRecipeWrapper(recipe, guiHelper, background, u, v, width, height);
        }
    }
}
