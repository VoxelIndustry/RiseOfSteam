package net.qbar.common.recipe.category;

import lombok.Getter;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class QBarRecipeCategory
{
    private String           name;
    private List<QBarRecipe> recipes;

    public QBarRecipeCategory(String name)
    {
        this.name = name;

        this.recipes = new ArrayList<>();
    }

    public <T> boolean inputMatchWithoutCount(final int recipeSlot, final T ingredient)
    {
        return this.recipes.stream().anyMatch(recipe ->
        {
            if (!recipe.hasInputType(ingredient.getClass())
                    || recipe.getRecipeInputs(ingredient.getClass()).size() < recipeSlot)
                return false;
            return ((RecipeIngredient<T>) recipe.getRecipeInputs(ingredient.getClass()).get(recipeSlot))
                    .match(ingredient);
        });
    }

    public <T> boolean inputMatchWithCount(final int recipeSlot, final T ingredient)
    {
        return this.recipes.stream().anyMatch(recipe ->
        {
            if (!recipe.hasInputType(ingredient.getClass())
                    || recipe.getRecipeInputs(ingredient.getClass()).size() < recipeSlot)
                return false;
            return ((RecipeIngredient<T>) recipe.getRecipeInputs(ingredient.getClass()).get(recipeSlot))
                    .matchWithQuantity(ingredient);
        });
    }

    public Optional<QBarRecipe> getRecipe(Object... inputs)
    {
        return this.recipes.stream().filter(recipe ->
        {
            int i = 0;
            for (final Object ingredient : inputs)
            {
                if (!recipe.hasInputType(ingredient.getClass())
                        || i >= recipe.getRecipeInputs(ingredient.getClass()).size())
                    break;
                if (!((RecipeIngredient<Object>) recipe.getRecipeInputs(ingredient.getClass()).get(i))
                        .matchWithQuantity(ingredient))
                    return false;
                i++;
            }
            return true;
        }).findFirst();
    }

    public void add(QBarRecipe recipe)
    {
        recipes.add(recipe);
    }
}
