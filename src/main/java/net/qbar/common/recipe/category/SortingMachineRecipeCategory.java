package net.qbar.common.recipe.category;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.qbar.common.init.QBarItems;
import net.qbar.common.ore.OreStack;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;
import net.qbar.common.recipe.ingredient.OreStackRecipeIngredient;

import java.util.Optional;
import java.util.Random;

public class SortingMachineRecipeCategory extends QBarRecipeCategory
{
    private Random rand = new Random();

    public SortingMachineRecipeCategory(String name)
    {
        super(name);
    }

    public <T> boolean inputMatchWithoutCount(final int recipeSlot, final T ingredient)
    {
        if (recipeSlot == 0 && ingredient instanceof ItemStack
                && ((ItemStack) ingredient).getItem() == QBarItems.MIXED_RAW_ORE)
            return true;
        return false;
    }

    public <T> boolean inputMatchWithCount(final int recipeSlot, final T ingredient)
    {
        if (recipeSlot == 0 && ingredient instanceof ItemStack
                && ((ItemStack) ingredient).getItem() == QBarItems.MIXED_RAW_ORE)
            return true;
        return false;
    }

    public Optional<QBarRecipe> getRecipe(Object... inputs)
    {
        if (inputs.length >= 1 && inputs[0] instanceof ItemStack)
        {
            ItemStack mixedOre = (ItemStack) inputs[0];

        }
        return Optional.empty();
    }

    private static final class SortingMachineRecipe extends QBarRecipe
    {
        public SortingMachineRecipe(ItemStack inputMixedOre, OreStack... products)
        {
            this.inputs.put(ItemStack.class, NonNullList.withSize(1, new ItemStackRecipeIngredient(inputMixedOre)));

            this.outputs.put(OreStack.class, NonNullList.create());
            for (OreStack stack : products)
                this.getRecipeOutputs(OreStack.class).add(new OreStackRecipeIngredient(stack));
        }

        @Override
        public int getTime()
        {
            return 10;
        }
    }
}
