package net.ros.common.recipe.category;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.init.ROSItems;
import net.ros.common.ore.Mineral;
import net.ros.common.ore.MineralDensity;
import net.ros.common.ore.Ore;
import net.ros.common.ore.Ores;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.recipe.ingredient.FluidStackRecipeIngredient;
import net.ros.common.recipe.ingredient.ItemStackRecipeIngredient;
import net.ros.common.recipe.ingredient.RecipeIngredient;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class OreWasherRecipeCategory extends RecipeCategory
{
    private Random rand = new Random();

    public OreWasherRecipeCategory(String name)
    {
        super(name);
    }

    @Override
    public <T> boolean inputMatchWithoutCount(final int recipeSlot, final T ingredient)
    {
        if (recipeSlot == 0 && ingredient instanceof FluidStack
                && Ores.isSludge(((FluidStack) ingredient).getFluid()))
            return true;
        if (recipeSlot == 1 && ingredient instanceof FluidStack
                && ((FluidStack) ingredient).getFluid() == FluidRegistry.WATER)
            return true;
        return false;
    }

    @Override
    public <T> boolean inputMatchWithCount(final int recipeSlot, final T ingredient)
    {
        if (recipeSlot == 0 && ingredient instanceof FluidStack
                && Ores.isSludge(((FluidStack) ingredient).getFluid()) &&
                ((FluidStack) ingredient).amount >= Fluid.BUCKET_VOLUME)
            return true;
        if (recipeSlot == 1 && ingredient instanceof FluidStack
                && ((FluidStack) ingredient).getFluid() == FluidRegistry.WATER
                && ((FluidStack) ingredient).amount >= Fluid.BUCKET_VOLUME)
            return true;
        return false;
    }

    public Optional<RecipeBase> getRecipe(Object... inputs)
    {
        if (inputs.length < 2)
            return Optional.empty();
        if (!this.inputMatchWithCount(0, inputs[0]) || !this.inputMatchWithCount(0, inputs[1]))
            return Optional.empty();

        FluidStack sludge = (FluidStack) inputs[0];
        FluidStack washingFluid = (FluidStack) inputs[1];

        Optional<Ore> ore = Ores.fromSludge(sludge.getFluid());

        if (!ore.isPresent())
            return Optional.empty();

        ItemStack rawOre = ItemStack.EMPTY;
        ItemStack leftOver = ItemStack.EMPTY;
        Integer yield = washingFluid.getFluid() == FluidRegistry.WATER ? 0 : 10;
        float leftOverChance = 1;

        if (ore.get().getMinerals().size() == 1)
        {
            for (Map.Entry<Mineral, Float> mineral: ore.get().getMinerals().entrySet())
            {
                if (mineral.getValue() + yield >= 0.25f)
                {
                    rawOre = Ores.getRawMineral(mineral.getKey(),
                            MineralDensity.fromValue(mineral.getValue() + yield));

                    leftOverChance -= mineral.getValue();
                }
            }
        }
        else
        {
            rawOre = new ItemStack(ROSItems.MIXED_RAW_ORE);
            rawOre.setTagCompound(new NBTTagCompound());

            int i = 0;
            for (Map.Entry<Mineral, Float> mineral: ore.get().getMinerals().entrySet())
            {
                if (mineral.getValue() + yield >= 0.25f)
                {
                    rawOre.getTagCompound().setString("ore" + i, mineral.getKey().getName());
                    rawOre.getTagCompound().setString("density" + i, mineral.getValue() + yield >= 0.75f ?
                            "rich" : (mineral.getValue() + yield >= 0.50f ? "normal" : "poor"));
                    leftOverChance -= mineral.getValue();
                    i++;
                }
            }
            if (i != 0)
                rawOre.getTagCompound().setInteger("oreCount", i);
            else
                rawOre = ItemStack.EMPTY;
        }

        if (leftOverChance > 0)
        {
            if (leftOverChance >= this.rand.nextFloat())
                leftOver = new ItemStack(this.rand.nextBoolean() ? Blocks.DIRT : Blocks.GRAVEL);
        }

        FluidStack sludgeCopy = sludge.copy();
        sludgeCopy.amount = Fluid.BUCKET_VOLUME;

        return Optional.of(new OreWasherRecipe(sludgeCopy, rawOre, leftOver));
    }

    private static final class OreWasherRecipe extends RecipeBase
    {
        public OreWasherRecipe(FluidStack inputSludge, ItemStack rawOre, ItemStack leftOver)
        {
            NonNullList<RecipeIngredient<?>> ingredients = NonNullList.create();
            ingredients.add(new FluidStackRecipeIngredient(inputSludge));
            ingredients.add(new FluidStackRecipeIngredient(new FluidStack(FluidRegistry.WATER, 100)));

            this.inputs.put(FluidStack.class, ingredients);

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
