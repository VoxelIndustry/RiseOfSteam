package net.qbar.common.recipe.category;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.qbar.common.init.QBarItems;
import net.qbar.common.ore.QBarOre;
import net.qbar.common.ore.SludgeData;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class OreWasherRecipeCategory extends QBarRecipeCategory
{
    private Random rand = new Random();

    public OreWasherRecipeCategory(String name)
    {
        super(name);
    }

    public <T> boolean inputMatchWithoutCount(final int recipeSlot, final T ingredient)
    {
        if (recipeSlot == 0 && ingredient instanceof ItemStack
                && ((ItemStack) ingredient).getItem() == QBarItems.MINERAL_SLUDGE)
            return true;
        return false;
    }

    public <T> boolean inputMatchWithCount(final int recipeSlot, final T ingredient)
    {
        if (recipeSlot == 0 && ingredient instanceof ItemStack
                && ((ItemStack) ingredient).getItem() == QBarItems.MINERAL_SLUDGE)
            return true;
        return false;
    }

    public Optional<QBarRecipe> getRecipe(Object... inputs)
    {
        if (inputs[0] instanceof ItemStack)
        {
            ItemStack sludge = (ItemStack) inputs[0];

            SludgeData data = SludgeData.fromNBT(sludge.getTagCompound().getCompoundTag("sludgeData"));

            ItemStack rawOre = new ItemStack(QBarItems.MIXED_RAW_ORE);
            ItemStack leftOver = ItemStack.EMPTY;
            Integer yield = (Integer) inputs[1];
            float leftOverChance = 1;

            if (data.getOres().size() == 1)
            {
                rawOre = new ItemStack(QBarItems.RAW_ORE);
                rawOre.setTagCompound(new NBTTagCompound());

                for (Map.Entry<QBarOre, Float> ore : data.getOres().entrySet())
                {
                    rawOre.getTagCompound().setString("ore", ore.getKey().getName());
                    rawOre.getTagCompound().setString("density",
                            ore.getValue() + yield >= 75 ? "rich" : (ore.getValue() + yield >= 50 ? "normal" : "poor"));
                    leftOverChance -= ore.getValue();
                }
            }
            else
            {
                rawOre.setTagCompound(new NBTTagCompound());

                rawOre.getTagCompound().setInteger("oreCount", data.getOres().size());

                int i = 0;
                for (Map.Entry<QBarOre, Float> ore : data.getOres().entrySet())
                {
                    rawOre.getTagCompound().setString("ore" + i, ore.getKey().getName());
                    rawOre.getTagCompound().setString("density" + i,
                            ore.getValue() + yield >= 75 ? "rich" : (ore.getValue() + yield >= 50 ? "normal" : "poor"));
                    leftOverChance -= ore.getValue();
                    i++;
                }
            }

            if (leftOverChance > 0)
            {
                if (leftOverChance >= this.rand.nextFloat())
                    leftOver = new ItemStack(this.rand.nextBoolean() ? Blocks.DIRT : Blocks.GRAVEL);
            }

            return Optional.of(new OreWasherRecipe(sludge, rawOre, leftOver));
        }
        return Optional.empty();
    }

    private static final class OreWasherRecipe extends QBarRecipe
    {
        public OreWasherRecipe(ItemStack inputSludge, ItemStack rawOre, ItemStack leftOver)
        {
            this.inputs.put(ItemStack.class, NonNullList.withSize(1, new ItemStackRecipeIngredient(inputSludge)));

            this.outputs.put(ItemStack.class, NonNullList.create());
            if (!rawOre.isEmpty())
                this.getRecipeOutputs(ItemStack.class).add(new ItemStackRecipeIngredient(rawOre));
            if (!leftOver.isEmpty())
                this.getRecipeOutputs(ItemStack.class).add(new ItemStackRecipeIngredient(leftOver));
        }

        @Override
        public int getTime()
        {
            return 10;
        }
    }
}
