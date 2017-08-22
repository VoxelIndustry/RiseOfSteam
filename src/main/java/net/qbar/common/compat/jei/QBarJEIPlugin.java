package net.qbar.common.compat.jei;

import lombok.Getter;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.qbar.client.gui.GuiRollingMill;
import net.qbar.client.gui.GuiSawMill;
import net.qbar.client.gui.GuiSteamFurnace;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.recipe.type.RollingMillRecipe;
import net.qbar.common.recipe.type.SawMillRecipe;

@JEIPlugin
@Getter
public class QBarJEIPlugin implements IModPlugin
{
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(QBarJEIRecipeCategory.builder(registry.getJeiHelpers().getGuiHelper())
                .background(GuiRollingMill.BACKGROUND).uid(QBarRecipeHandler.ROLLINGMILL_UID)
                .title("gui.rollingmill.name").u(46).v(16).width(91).height(54)
                .input(0, 19).output(69, 18).create());

        registry.addRecipeCategories(QBarJEIRecipeCategory.builder(registry.getJeiHelpers().getGuiHelper())
                .background(GuiSawMill.BACKGROUND).uid(QBarRecipeHandler.SAW_MILL_UID)
                .title("gui.sawmill.name").u(46).v(16).width(91).height(54)
                .input(0, 19).output(69, 18).create());
    }

    @Override
    public void register(final IModRegistry registry)
    {
        QBarRecipeHandler.RECIPES.forEach((name, category) -> registry.addRecipes(category.getRecipes(), name));

        registry.addRecipeCatalyst(new ItemStack(QBarBlocks.ROLLING_MILL), QBarRecipeHandler.ROLLINGMILL_UID);
        registry.addRecipeCatalyst(new ItemStack(QBarBlocks.SAWMILL), QBarRecipeHandler.SAW_MILL_UID);

        QBarJEIRecipeWrapper.Builder rollingMillWrapper = QBarJEIRecipeWrapper.builder(registry.getJeiHelpers().getGuiHelper())
                .background(GuiRollingMill.BACKGROUND).u(176).v(14).width(24).height(17);
        registry.handleRecipes(RollingMillRecipe.class, rollingMillWrapper::create, QBarRecipeHandler.ROLLINGMILL_UID);

        QBarJEIRecipeWrapper.Builder sawMillWrapper = QBarJEIRecipeWrapper.builder(registry.getJeiHelpers().getGuiHelper())
                .background(GuiSawMill.BACKGROUND).u(176).v(14).width(24).height(17);
        registry.handleRecipes(SawMillRecipe.class, sawMillWrapper::create, QBarRecipeHandler.SAW_MILL_UID);

        registry.addRecipeClickArea(GuiRollingMill.class, 80, 35, 26, 20, QBarRecipeHandler.ROLLINGMILL_UID);
        registry.addRecipeClickArea(GuiSteamFurnace.class, 80, 35, 26, 20, VanillaRecipeCategoryUid.SMELTING);
        registry.addRecipeClickArea(GuiSawMill.class, 80, 35, 26, 20, QBarRecipeHandler.SAW_MILL_UID);
    }
}