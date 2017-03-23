package net.qbar.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RollingMillRecipe extends QBarRecipe
{
    private final NonNullList<ItemStack> input;
    private final NonNullList<ItemStack> output;

    public RollingMillRecipe(final ItemStack metal, final ItemStack plate)
    {
        this.input = NonNullList.withSize(1, metal);
        this.output = NonNullList.withSize(1, plate);
    }

    @Override
    public NonNullList<ItemStack> getRecipeInputs()
    {
        return this.input;
    }

    @Override
    public NonNullList<ItemStack> getRecipeOutputs()
    {
        return this.output;
    }
}
