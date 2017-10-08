package net.qbar.common.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.qbar.common.QBarConstants;
import net.qbar.common.recipe.category.QBarRecipeCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class QBarRecipeHandler
{
    public static final String ROLLINGMILL_UID     = QBarConstants.MODID + ".rollingmill";
    public static final String FURNACE_UID         = QBarConstants.MODID + ".furnace";
    public static final String LIQUIDBOILER_UID    = QBarConstants.MODID + ".liquidboiler";
    public static final String ORE_WASHER_UID      = QBarConstants.MODID + ".orewasher";
    public static final String SORTING_MACHINE_UID = QBarConstants.MODID + ".sortingmachine";
    public static final String SAW_MILL_UID        = QBarConstants.MODID + ".sawmill";

    public static final HashMap<String, QBarRecipeCategory> RECIPES = new HashMap<>();

    public static final ArrayList<IRecipe> CRAFTING_RECIPES = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithoutCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        return QBarRecipeHandler.RECIPES.containsKey(recipeID) &&
                QBarRecipeHandler.RECIPES.get(recipeID).inputMatchWithoutCount(recipeSlot, ingredient);
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        return QBarRecipeHandler.RECIPES.containsKey(recipeID) &&
                QBarRecipeHandler.RECIPES.get(recipeID).inputMatchWithCount(recipeSlot, ingredient);
    }

    @SuppressWarnings("unchecked")
    public static Optional<QBarRecipe> getRecipe(final String recipeID, final Object... inputs)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
            return QBarRecipeHandler.RECIPES.get(recipeID).getRecipe(inputs);
        return Optional.empty();
    }
}
