package net.ros.common.compat.jei;

import lombok.Getter;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.ros.client.gui.GuiRollingMill;
import net.ros.client.gui.GuiSawMill;
import net.ros.client.gui.GuiSteamFurnace;
import net.ros.common.init.ROSBlocks;
import net.ros.common.recipe.RecipeHandler;
import net.ros.common.recipe.type.RollingMillRecipe;
import net.ros.common.recipe.type.SawMillRecipe;

@JEIPlugin
@Getter
public class ROSJEIPlugin implements IModPlugin
{
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(JEIRecipeCategory.builder(registry.getJeiHelpers().getGuiHelper())
                .background(GuiRollingMill.BACKGROUND).uid(RecipeHandler.ROLLINGMILL_UID)
                .title("gui.rollingmill.name").u(46).v(16).width(91).height(54)
                .input(0, 19).output(69, 18).create());

        registry.addRecipeCategories(JEIRecipeCategory.builder(registry.getJeiHelpers().getGuiHelper())
                .background(GuiSawMill.BACKGROUND).uid(RecipeHandler.SAW_MILL_UID)
                .title("gui.sawmill.name").u(46).v(16).width(91).height(54)
                .input(0, 19).output(69, 18).create());
    }

    @Override
    public void register(final IModRegistry registry)
    {
        RecipeHandler.RECIPES.forEach((name, category) -> registry.addRecipes(category.getRecipes(), name));

        registry.addRecipeCatalyst(new ItemStack(ROSBlocks.ROLLING_MILL), RecipeHandler.ROLLINGMILL_UID);
        registry.addRecipeCatalyst(new ItemStack(ROSBlocks.SAWMILL), RecipeHandler.SAW_MILL_UID);

        JEIRecipeWrapper.Builder rollingMillWrapper = JEIRecipeWrapper.builder(registry.getJeiHelpers().getGuiHelper())
                .background(GuiRollingMill.BACKGROUND).u(176).v(14).width(24).height(17);
        registry.handleRecipes(RollingMillRecipe.class, rollingMillWrapper::create, RecipeHandler.ROLLINGMILL_UID);

        JEIRecipeWrapper.Builder sawMillWrapper = JEIRecipeWrapper.builder(registry.getJeiHelpers().getGuiHelper())
                .background(GuiSawMill.BACKGROUND).u(176).v(14).width(24).height(17);
        registry.handleRecipes(SawMillRecipe.class, sawMillWrapper::create, RecipeHandler.SAW_MILL_UID);

        registry.addRecipeClickArea(GuiRollingMill.class, 80, 35, 26, 20, RecipeHandler.ROLLINGMILL_UID);
        registry.addRecipeClickArea(GuiSteamFurnace.class, 80, 35, 26, 20, VanillaRecipeCategoryUid.SMELTING);
        registry.addRecipeClickArea(GuiSawMill.class, 80, 35, 26, 20, RecipeHandler.SAW_MILL_UID);
    }
}