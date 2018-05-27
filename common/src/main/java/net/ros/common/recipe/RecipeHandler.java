package net.ros.common.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.ros.common.ROSConstants;
import net.ros.common.recipe.category.RecipeCategory;

import java.util.*;

public class RecipeHandler
{
    public static final String ROLLINGMILL_UID     = ROSConstants.MODID + ".rollingmill";
    public static final String FURNACE_UID         = ROSConstants.MODID + ".furnace";
    public static final String LIQUIDBOILER_UID    = ROSConstants.MODID + ".liquidboiler";
    public static final String ORE_WASHER_UID      = ROSConstants.MODID + ".orewasher";
    public static final String SORTING_MACHINE_UID = ROSConstants.MODID + ".sortingmachine";
    public static final String SAW_MILL_UID        = ROSConstants.MODID + ".sawmill";
    public static final String MELTING_UID         = ROSConstants.MODID + ".melting";
    public static final String ALLOY_UID           = ROSConstants.MODID + ".alloying";

    public static final String BLUEPRINT_UID = ROSConstants.MODID + ".blueprint";

    public static final HashMap<String, RecipeCategory> RECIPES = new HashMap<>();

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
        return RecipeHandler.RECIPES.containsKey(recipeID) &&
                RecipeHandler.RECIPES.get(recipeID).inputMatchWithoutCount(recipeSlot, ingredient);
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
        return RecipeHandler.RECIPES.containsKey(recipeID) &&
                RecipeHandler.RECIPES.get(recipeID).inputMatchWithCount(recipeSlot, ingredient);
    }

    /**
     * Retrieve a specific <code>RecipeBase</code> from the category containing it and a list of generic ingredients.
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
    public static Optional<RecipeBase> getRecipe(final String recipeID, final Object... inputs)
    {
        if (RecipeHandler.RECIPES.containsKey(recipeID))
            return RecipeHandler.RECIPES.get(recipeID).getRecipe(inputs);
        return Optional.empty();
    }

    public static List<RecipeBase> getRecipesLike(String recipeID, Object... inputs)
    {
        if (RecipeHandler.RECIPES.containsKey(recipeID))
            return RecipeHandler.RECIPES.get(recipeID).getRecipesLike(inputs);
        return Collections.emptyList();
    }
}
