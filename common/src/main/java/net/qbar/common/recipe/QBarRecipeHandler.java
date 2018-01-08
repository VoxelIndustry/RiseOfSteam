package net.qbar.common.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.qbar.common.QBarConstants;
import net.qbar.common.recipe.category.QBarRecipeCategory;

import java.util.*;

public class QBarRecipeHandler
{
    public static final String ROLLINGMILL_UID     = QBarConstants.MODID + ".rollingmill";
    public static final String FURNACE_UID         = QBarConstants.MODID + ".furnace";
    public static final String LIQUIDBOILER_UID    = QBarConstants.MODID + ".liquidboiler";
    public static final String ORE_WASHER_UID      = QBarConstants.MODID + ".orewasher";
    public static final String SORTING_MACHINE_UID = QBarConstants.MODID + ".sortingmachine";
    public static final String SAW_MILL_UID        = QBarConstants.MODID + ".sawmill";
    public static final String MELTING_UID         = QBarConstants.MODID + ".melting";
    public static final String ALLOY_UID           = QBarConstants.MODID + ".alloying";

    public static final HashMap<String, QBarRecipeCategory> RECIPES = new HashMap<>();

    public static final ArrayList<IRecipe> CRAFTING_RECIPES = new ArrayList<>();

    /**
     * Check if a given indexed ingredient match a recipe.
     * Only shaped recipes are currently supported.
     * The matching logic is deferred to the <code>RecipeCategory</code> which by default implementation
     * will defer this logic to the <code>RecipeIngredient</code> wrapper after retrieving the specified index.
     * This method does not take into account the quantity of the ingredient.
     *
     * @param recipeID   of the category to check
     * @param recipeSlot of the ingredient
     * @param ingredient to match
     * @param <T>        a generic ingredient that can be wrapped by the recipe in a <code>RecipeIngredient</code>
     *                   instance
     * @return the result of the match
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithoutCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        return QBarRecipeHandler.RECIPES.containsKey(recipeID) &&
                QBarRecipeHandler.RECIPES.get(recipeID).inputMatchWithoutCount(recipeSlot, ingredient);
    }

    /**
     * Check if a given indexed ingredient match a recipe.
     * Only shaped recipes are currently supported.
     * The matching logic is deferred to the <code>RecipeCategory</code> which by default implementation
     * will defer this logic to the <code>RecipeIngredient</code> wrapper after retrieving the specified index.
     * This method does take into account the quantity of the ingredient,
     * it must be at least equals or superior to the recipe expected quantity.
     *
     * @param recipeID   of the category to check
     * @param recipeSlot of the ingredient
     * @param ingredient to match
     * @param <T>        a generic ingredient that can be wrapped by the recipe in a <code>RecipeIngredient</code>
     *                   instance
     * @return the result of the match
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        return QBarRecipeHandler.RECIPES.containsKey(recipeID) &&
                QBarRecipeHandler.RECIPES.get(recipeID).inputMatchWithCount(recipeSlot, ingredient);
    }

    /**
     * Retrieve a specific <code>QBarRecipe</code> from the category containing it and a list of generic ingredients.
     * By default implementation the <code>RecipeCategory</code> will attempt to match the list of ingredients
     * against all its registered recipes.
     * Many custom categories will return a fake recipe for dynamic evaluation.
     * <p>
     * This method has a high performance cost and its return should be always cached.
     *
     * @param recipeID of the category to check
     * @param inputs   an index-ordered list of arbitrary generic ingredients
     * @return an Optional containing the found recipe or null in case of failure
     */
    @SuppressWarnings("unchecked")
    public static Optional<QBarRecipe> getRecipe(final String recipeID, final Object... inputs)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
            return QBarRecipeHandler.RECIPES.get(recipeID).getRecipe(inputs);
        return Optional.empty();
    }

    public static List<QBarRecipe> getRecipesLike(String recipeID, Object... inputs)
    {
        if(QBarRecipeHandler.RECIPES.containsKey(recipeID))
            return QBarRecipeHandler.RECIPES.get(recipeID).getRecipesLike(inputs);
        return Collections.emptyList();
    }
}
