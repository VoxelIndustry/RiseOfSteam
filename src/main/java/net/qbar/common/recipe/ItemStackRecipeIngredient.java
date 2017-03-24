package net.qbar.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.qbar.common.util.ItemUtils;

public class ItemStackRecipeIngredient extends RecipeIngredient<ItemStack>
{
    private final String    oreDict;
    private final int       quantity;
    private final ItemStack cachedStack;
    private final ItemStack ingredient;

    public ItemStackRecipeIngredient(final ItemStack ingredient)
    {
        this.ingredient = ingredient;
        this.oreDict = null;
        this.quantity = 0;

        this.cachedStack = ItemStack.EMPTY;
    }

    public ItemStackRecipeIngredient(final String oreDict, final int quantity)
    {
        this.ingredient = ItemStack.EMPTY;
        this.oreDict = oreDict;
        this.quantity = quantity;

        this.cachedStack = OreDictionary.getOres(oreDict).get(0).copy();
        this.cachedStack.setCount(quantity);
    }

    @Override
    public boolean match(final ItemStack against)
    {
        if (!this.ingredient.isEmpty())
            return ItemUtils.deepEquals(this.ingredient, against);
        else
            return OreDictionary.getOres(this.oreDict).stream().anyMatch(stack -> ItemUtils.deepEquals(stack, against));
    }

    @Override
    public boolean matchWithQuantity(final ItemStack against)
    {
        if (!this.ingredient.isEmpty())
            return this.ingredient.getCount() <= against.getCount() && ItemUtils.deepEquals(this.ingredient, against);
        else
            return this.quantity <= against.getCount() && OreDictionary.getOres(this.oreDict).stream()
                    .anyMatch(stack -> ItemUtils.deepEquals(stack, against));
    }

    @Override
    public int getQuantity()
    {
        if (!this.ingredient.isEmpty())
            return this.ingredient.getCount();
        return this.quantity;
    }

    @Override
    public ItemStack getRawIngredient()
    {
        if (!this.ingredient.isEmpty())
            return this.ingredient;
        return this.cachedStack;
    }
}
