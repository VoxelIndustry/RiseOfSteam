package net.qbar.common.compat.jei;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import net.qbar.client.gui.GuiRollingMill;
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

        registry.addRecipes(QBarRecipeHandler.ROLLINGMILL);

        registry.addRecipeCategories(new RollingMillRecipeCategory(guiHelper));

        registry.addRecipeCategoryCraftingItem(new ItemStack(QBarBlocks.ROLLING_MILL),
                QBarRecipeHandler.ROLLINGMILL_UID);

        registry.addRecipeHandlers(new RollingMillRecipeHandler(jeiHelpers));

        registry.addRecipeClickArea(GuiRollingMill.class, 80, 35, 26, 20, QBarRecipeHandler.ROLLINGMILL_UID);
    }
}