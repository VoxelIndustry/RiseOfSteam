package net.ros.common.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.recipe.ingredient.RecipeIngredient;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class JEIRecipeWrapper implements IRecipeWrapper
{
    private final IDrawableAnimated arrow;

    private final RecipeBase recipe;

    private int x, y;

    JEIRecipeWrapper(RecipeBase recipe, IGuiHelper guiHelper, ResourceLocation arrow, int u, int v,
                     int width, int height, int x, int y)
    {
        this.recipe = recipe;

        final IDrawableStatic arrowStatic = guiHelper.createDrawable(arrow, u, v, width, height);
        this.arrow = guiHelper.createAnimatedDrawable(arrowStatic, 20, IDrawableAnimated.StartDirection.LEFT, false);

        this.x = x;
        this.y = y;
    }

    static Builder builder(IGuiHelper guiHelper)
    {
        return new Builder(guiHelper);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        this.arrow.draw(minecraft, x, y);
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputs(ItemStack.class, this.recipe.getRecipeInputs(ItemStack.class).stream()
                .map(RecipeIngredient::getRaw).collect(Collectors.toList()));
        ingredients.setInputs(FluidStack.class, this.recipe.getRecipeInputs(FluidStack.class).stream()
                .map(RecipeIngredient::getRaw).collect(Collectors.toList()));

        ingredients.setOutputs(ItemStack.class, this.recipe.getRecipeOutputs(ItemStack.class).stream()
                .map(RecipeIngredient::getRaw).collect(Collectors.toList()));
        ingredients.setOutputs(FluidStack.class, this.recipe.getRecipeOutputs(FluidStack.class).stream()
                .map(RecipeIngredient::getRaw).collect(Collectors.toList()));
    }

    public static class Builder
    {
        private ResourceLocation arrow;
        private int              u, v, width, height, x, y;
        private final IGuiHelper guiHelper;

        public Builder(IGuiHelper guiHelper)
        {
            this.guiHelper = guiHelper;

            this.x = 33;
            this.y = 19;
        }

        public Builder arrow(ResourceLocation arrow)
        {
            this.arrow = arrow;
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

        public Builder x(int x)
        {
            this.x = x;
            return this;
        }

        public Builder y(int y)
        {
            this.y = y;
            return this;
        }

        @Nonnull
        public JEIRecipeWrapper create(RecipeBase recipe)
        {
            return new JEIRecipeWrapper(recipe, guiHelper, arrow, u, v, width, height, x, y);
        }
    }
}
