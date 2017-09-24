package net.qbar.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.qbar.common.recipe.ingredient.RecipeIngredient;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class QBarRecipe
{
    protected final Map<Class<?>, NonNullList<RecipeIngredient<?>>> inputs  = new IdentityHashMap<Class<?>, NonNullList<RecipeIngredient<?>>>();
    protected final Map<Class<?>, NonNullList<RecipeIngredient<?>>> outputs = new IdentityHashMap<Class<?>, NonNullList<RecipeIngredient<?>>>();

    public boolean hasInputType(final Class<?> input)
    {
        return this.inputs.containsKey(input);
    }

    public boolean hasOutputType(final Class<?> output)
    {
        return this.outputs.containsKey(output);
    }

    @SuppressWarnings("unchecked")
    public <T> NonNullList<RecipeIngredient<T>> getRecipeInputs(final Class<T> clazz)
    {
        if (this.hasInputType(clazz))
            return (NonNullList<RecipeIngredient<T>>) (Object) this.inputs.get(clazz);
        return NonNullList.create();
    }

    @SuppressWarnings("unchecked")
    public <T> NonNullList<RecipeIngredient<T>> getRecipeOutputs(final Class<T> clazz)
    {
        if (this.hasOutputType(clazz))
            return (NonNullList<RecipeIngredient<T>>) (Object) this.outputs.get(clazz);
        return NonNullList.create();
    }

    public Optional<NonNullList<ItemStack>> getRemainingItems()
    {
        return Optional.empty();
    }

    public abstract int getTime();
}
