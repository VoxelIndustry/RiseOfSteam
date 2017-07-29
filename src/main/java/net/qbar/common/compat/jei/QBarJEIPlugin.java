package net.qbar.common.compat.jei;

import lombok.Getter;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.qbar.client.gui.GuiRollingMill;
import net.qbar.client.gui.GuiSteamFurnace;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.recipe.RollingMillRecipe;

@JEIPlugin
@Getter
public class QBarJEIPlugin implements IModPlugin
{
    private IJeiHelpers jeiHelpers;
    private IGuiHelper  guiHelper;

    private QBarJEIPlugin()
    {
    }

    private static QBarJEIPlugin INSTANCE;

    public static QBarJEIPlugin instance()
    {
        if (INSTANCE == null)
            INSTANCE = new QBarJEIPlugin();
        return INSTANCE;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        this.jeiHelpers = registry.getJeiHelpers();
        this.guiHelper = this.jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new RollingMillRecipeCategory(guiHelper));
    }

    @Override
    public void register(final IModRegistry registry)
    {
        QBarRecipeHandler.RECIPES.forEach((name, category) -> registry.addRecipes(category.getRecipes(), name));

        registry.addRecipeCatalyst(new ItemStack(QBarBlocks.ROLLING_MILL),
                QBarRecipeHandler.ROLLINGMILL_UID);

        registry.handleRecipes(RollingMillRecipe.class, RollingMillRecipeWrapper::new, QBarRecipeHandler.ROLLINGMILL_UID);

        registry.addRecipeClickArea(GuiRollingMill.class, 80, 35, 26, 20, QBarRecipeHandler.ROLLINGMILL_UID);
        registry.addRecipeClickArea(GuiSteamFurnace.class, 80, 35, 26, 20, VanillaRecipeCategoryUid.SMELTING);
    }
}