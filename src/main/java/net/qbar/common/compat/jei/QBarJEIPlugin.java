package net.qbar.common.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.qbar.client.gui.GuiRollingMill;
import net.qbar.client.gui.GuiSteamFurnace;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.recipe.QBarRecipeHandler;

@JEIPlugin
public class QBarJEIPlugin extends BlankModPlugin
{
    @Override
    public void register(final IModRegistry registry)
    {
        final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipes(QBarRecipeHandler.RECIPES.values());

        registry.addRecipeCategories(new RollingMillRecipeCategory(guiHelper));

        registry.addRecipeCategoryCraftingItem(new ItemStack(QBarBlocks.ROLLING_MILL),
                QBarRecipeHandler.ROLLINGMILL_UID);

        registry.addRecipeHandlers(new RollingMillRecipeHandler(jeiHelpers));

        registry.addRecipeClickArea(GuiRollingMill.class, 80, 35, 26, 20, QBarRecipeHandler.ROLLINGMILL_UID);
        registry.addRecipeClickArea(GuiSteamFurnace.class, 80, 35, 26, 20, VanillaRecipeCategoryUid.SMELTING);
    }
}