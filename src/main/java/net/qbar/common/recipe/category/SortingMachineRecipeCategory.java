package net.qbar.common.recipe.category;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.qbar.common.init.QBarItems;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;

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

            SortingMachineRecipe recipe = new SortingMachineRecipe(mixedOre.copy());

            if (mixedOre.hasTagCompound())
            {
                NBTTagCompound tag = mixedOre.getTagCompound();
                for (int i = 0; i < tag.getInteger("oreCount"); i++)
                {
                    ItemStack ore = new ItemStack(QBarItems.RAW_ORE);
                    ore.setTagCompound(new NBTTagCompound());
                    ore.getTagCompound().setString("ore", tag.getString("ore" + i));
                    ore.getTagCompound().setString("density", tag.getString("density" + i));

                    recipe.getRecipeOutputs(ItemStack.class).add(new ItemStackRecipeIngredient(ore));
                }
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    public static final class SortingMachineRecipe extends QBarRecipe
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
