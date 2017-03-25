package net.qbar.common.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.qbar.client.gui.GuiRollingMill;
import net.qbar.common.recipe.QBarRecipeHandler;

public class RollingMillRecipeCategory extends BlankRecipeCategory<RollingMillRecipeWrapper>
{
    private final IDrawable background;
    private final String    title;

    public RollingMillRecipeCategory(final IGuiHelper guiHelper)
    {
        this.background = guiHelper.createDrawable(GuiRollingMill.BACKGROUND, 46, 16, 91, 54);
        this.title = I18n.translateToLocal("gui.rollingmill.name");
    }

    @Override
    public String getUid()
    {
        return QBarRecipeHandler.ROLLINGMILL_UID;
    }

    @Override
    public String getTitle()
    {
        return this.title;
    }

    @Override
    public IDrawable getBackground()
    {
        return this.background;
    }

    @Override
    public void setRecipe(final IRecipeLayout recipeLayout, final RollingMillRecipeWrapper recipeWrapper,
            final IIngredients ingredients)
    {
        final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, true, 0, 19);
        guiItemStacks.init(1, false, 69, 18);

        guiItemStacks.set(0, ingredients.getInputs(ItemStack.class).get(0));
        guiItemStacks.set(1, ingredients.getOutputs(ItemStack.class).get(0));
    }
}
