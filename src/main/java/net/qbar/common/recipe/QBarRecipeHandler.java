package net.qbar.common.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.QBar;
import net.qbar.common.recipe.category.FurnaceRecipeCategory;
import net.qbar.common.recipe.category.OreWasherRecipeCategory;
import net.qbar.common.recipe.category.QBarRecipeCategory;
import net.qbar.common.recipe.category.SortingMachineRecipeCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class QBarRecipeHandler
{
    public static final String ROLLINGMILL_UID     = QBar.MODID + ".rollingmill";
    public static final String FURNACE_UID         = QBar.MODID + ".furnace";
    public static final String LIQUIDBOILER_UID    = QBar.MODID + ".liquidboiler";
    public static final String ORE_WASHER_UID      = QBar.MODID + ".orewasher";
    public static final String SORTING_MACHINE_UID = QBar.MODID + ".sortingmachine";

    public static final HashMap<String, QBarRecipeCategory> RECIPES = new HashMap<>();

    public static final ArrayList<String> metals = new ArrayList<>();

    public static void registerRecipes()
    {
        QBarRecipeHandler.RECIPES.put(ROLLINGMILL_UID, new QBarRecipeCategory(ROLLINGMILL_UID));
        QBarRecipeHandler.RECIPES.put(LIQUIDBOILER_UID, new QBarRecipeCategory(LIQUIDBOILER_UID));
        QBarRecipeHandler.RECIPES.put(ORE_WASHER_UID, new OreWasherRecipeCategory(ORE_WASHER_UID));
        QBarRecipeHandler.RECIPES.put(FURNACE_UID, new FurnaceRecipeCategory(FURNACE_UID));
        QBarRecipeHandler.RECIPES.put(SORTING_MACHINE_UID, new SortingMachineRecipeCategory(SORTING_MACHINE_UID));

        QBarRecipeHandler.metals.forEach(metalName ->
        {
            QBarRecipeHelper.addIngotToPlateRecipe(metalName);
            QBarRecipeHelper.addBlockToPlateRecipe(metalName);
        });

        QBarRecipeHelper.addLiquidBoilerRecipe(FluidRegistry.LAVA, 2, 1200);
    }

    @SubscribeEvent
    public void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        event.getRegistry().register(new SludgeRecipe().setRegistryName(new ResourceLocation(QBar.MODID, "compressedsludge")));
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithoutCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
            return QBarRecipeHandler.RECIPES.get(recipeID).inputMatchWithoutCount(recipeSlot, ingredient);
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
            return QBarRecipeHandler.RECIPES.get(recipeID).inputMatchWithCount(recipeSlot, ingredient);
        return false;
    }

    @SuppressWarnings("unchecked")
    public static Optional<QBarRecipe> getRecipe(final String recipeID, final Object... inputs)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
            return QBarRecipeHandler.RECIPES.get(recipeID).getRecipe(inputs);
        return Optional.empty();
    }
}
