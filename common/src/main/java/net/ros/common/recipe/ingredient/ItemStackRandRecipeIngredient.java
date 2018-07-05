package net.ros.common.recipe.ingredient;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.Range;

public class ItemStackRandRecipeIngredient extends ItemStackRecipeIngredient
{
    private Range<Integer> range;

    public ItemStackRandRecipeIngredient(ItemStack ingredient, Range<Integer> range)
    {
        super(ingredient);

        this.range = range;
    }

    public ItemStackRandRecipeIngredient(String oreDict, int quantity, Range<Integer> range)
    {
        super(oreDict, quantity);

        this.range = range;
    }

    @Override
    public ItemStack getRaw()
    {
        ItemStack copy = this.ingredient.isEmpty() ? this.cachedStack.copy() : ingredient.copy();

        copy.setCount(RandomUtils.nextInt(this.range.getMinimum(), this.range.getMaximum()));
        return copy;
    }
}
