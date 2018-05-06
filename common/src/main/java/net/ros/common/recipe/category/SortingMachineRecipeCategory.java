package net.ros.common.recipe.category;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.ros.common.init.ROSItems;
import net.ros.common.ore.MineralDensity;
import net.ros.common.ore.Ores;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.recipe.ingredient.ItemStackRecipeIngredient;

import java.util.Optional;

public class SortingMachineRecipeCategory extends RecipeCategory
{
    public SortingMachineRecipeCategory(String name)
    {
        super(name);
    }

    public <T> boolean inputMatchWithoutCount(final int recipeSlot, final T ingredient)
    {
        if (recipeSlot == 0 && ingredient instanceof ItemStack
                && ((ItemStack) ingredient).getItem() == ROSItems.MIXED_RAW_ORE)
            return true;
        return false;
    }

    public <T> boolean inputMatchWithCount(final int recipeSlot, final T ingredient)
    {
        if (recipeSlot == 0 && ingredient instanceof ItemStack
                && ((ItemStack) ingredient).getItem() == ROSItems.MIXED_RAW_ORE)
            return true;
        return false;
    }

    public Optional<RecipeBase> getRecipe(Object... inputs)
    {
        if (inputs.length >= 1 && inputs[0] instanceof ItemStack)
        {
            ItemStack mixedOre = (ItemStack) inputs[0];

            SortingMachineRecipe recipe = new SortingMachineRecipe(mixedOre.copy());

            if (mixedOre.hasTagCompound())
            {
                NBTTagCompound tag = mixedOre.getTagCompound();
                for (int i = 0; i < tag.getInteger("oreCount"); i++)
                {
                    ItemStack ore = Ores.getRawMineral(
                            Ores.getMineralFromName(tag.getString("ore" + i)).get(),
                            MineralDensity.valueOf(tag.getString("density" + i).toUpperCase()));

                    recipe.getRecipeOutputs(ItemStack.class).add(new ItemStackRecipeIngredient(ore));
                }
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    public static final class SortingMachineRecipe extends RecipeBase
    {
        public SortingMachineRecipe(ItemStack inputMixedOre, ItemStack... products)
        {
            this.inputs.put(ItemStack.class, NonNullList.withSize(1, new ItemStackRecipeIngredient(inputMixedOre)));

            this.outputs.put(ItemStack.class, NonNullList.create());
            for (ItemStack stack : products)
                this.getRecipeOutputs(ItemStack.class).add(new ItemStackRecipeIngredient(stack));
        }

        @Override
        public int getTime()
        {
            return 10;
        }
    }
}
