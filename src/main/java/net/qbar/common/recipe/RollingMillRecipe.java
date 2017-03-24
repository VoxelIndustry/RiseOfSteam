package net.qbar.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RollingMillRecipe extends QBarRecipe
{
    public RollingMillRecipe(final ItemStackRecipeIngredient metal, final ItemStackRecipeIngredient plate)
    {
        this.inputs.put(ItemStack.class, NonNullList.withSize(1, metal));
        this.outputs.put(ItemStack.class, NonNullList.withSize(1, plate));
    }

    @Override
    public int getTime()
    {
        return 40;
    }
}
