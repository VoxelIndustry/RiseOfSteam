package net.qbar.common.recipe.category;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.qbar.common.init.QBarItems;
import net.qbar.common.ore.MineralDensity;
import net.qbar.common.ore.QBarMineral;
import net.qbar.common.ore.QBarOres;
import net.qbar.common.ore.SludgeData;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.ingredient.FluidStackRecipeIngredient;
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
                && (((ItemStack) ingredient).getItem() == QBarItems.MINERAL_SLUDGE
                || ((ItemStack) ingredient).getItem() == QBarItems.COMPRESSED_MINERAL_SLUDGE))
            return true;
        if (recipeSlot == 0 && ingredient instanceof FluidStack
                && ((FluidStack) ingredient).getFluid() == FluidRegistry.WATER)
            return true;
        return false;
    }

    public <T> boolean inputMatchWithCount(final int recipeSlot, final T ingredient)
    {
        if (recipeSlot == 0 && ingredient instanceof ItemStack
                && (((ItemStack) ingredient).getItem() == QBarItems.MINERAL_SLUDGE
                || ((ItemStack) ingredient).getItem() == QBarItems.COMPRESSED_MINERAL_SLUDGE))
            return true;
        if (recipeSlot == 0 && ingredient instanceof FluidStack
                && ((FluidStack) ingredient).getFluid() == FluidRegistry.WATER
                && ((FluidStack) ingredient).amount >= 100)
            return true;
        return false;
    }

    public Optional<QBarRecipe> getRecipe(Object... inputs)
    {
        if (inputs.length >= 2 && inputs[0] instanceof ItemStack && inputs[1] instanceof FluidStack)
        {
            ItemStack sludge = (ItemStack) inputs[0];

            if (((FluidStack) inputs[1]).amount >= 900 || (((FluidStack) inputs[1]).amount >= 100
                    && sludge.getItem() != QBarItems.COMPRESSED_MINERAL_SLUDGE))
            {
                SludgeData data = SludgeData.fromNBT(sludge.getTagCompound().getCompoundTag("sludgeData"));

                ItemStack rawOre = ItemStack.EMPTY;
                ItemStack leftOver = ItemStack.EMPTY;
                Integer yield = ((FluidStack) inputs[1]).getFluid() == FluidRegistry.WATER ? 0 : 10;
                float leftOverChance = 1;

                if (data.getOres().size() == 1)
                {
                    for (Map.Entry<QBarMineral, Float> ore : data.getOres().entrySet())
                    {
                        if (ore.getValue() + yield >= 0.25f)
                        {
                            rawOre = QBarOres.getRawMineral(ore.getKey(),
                                    MineralDensity.fromValue(ore.getValue() + yield));

                            leftOverChance -= ore.getValue();
                        }
                    }
                }
                else
                {
                    rawOre = new ItemStack(QBarItems.MIXED_RAW_ORE);
                    rawOre.setTagCompound(new NBTTagCompound());

                    int i = 0;
                    for (Map.Entry<QBarMineral, Float> ore : data.getOres().entrySet())
                    {
                        if (ore.getValue() + yield >= 0.25f)
                        {
                            rawOre.getTagCompound().setString("ore" + i, ore.getKey().getName());
                            rawOre.getTagCompound().setString("density" + i, ore.getValue() + yield >= 0.75f ? "rich"
                                    : (ore.getValue() + yield >= 0.50f ? "normal" : "poor"));
                            leftOverChance -= ore.getValue();
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

                    if (sludge.getItem() == QBarItems.COMPRESSED_MINERAL_SLUDGE)
                    {
                        for (int i = 0; i < 8; i++)
                        {
                            if (leftOverChance >= this.rand.nextFloat())
                            {
                                if (!leftOver.isEmpty())
                                    leftOver.grow(1);
                                else
                                    leftOver = new ItemStack(this.rand.nextBoolean() ? Blocks.DIRT : Blocks.GRAVEL);
                            }
                        }
                    }
                }

                if (sludge.getItem() == QBarItems.COMPRESSED_MINERAL_SLUDGE)
                    rawOre.grow(8);
                return Optional.of(new OreWasherRecipe(sludge.copy(), rawOre, leftOver,
                        sludge.getItem() == QBarItems.COMPRESSED_MINERAL_SLUDGE));
            }
        }
        return Optional.empty();
    }

    private static final class OreWasherRecipe extends QBarRecipe
    {
        private boolean compressedSludge;

        public OreWasherRecipe(ItemStack inputSludge, ItemStack rawOre, ItemStack leftOver, boolean compressedSludge)
        {
            this.compressedSludge = compressedSludge;

            this.inputs.put(ItemStack.class, NonNullList.withSize(1, new ItemStackRecipeIngredient(inputSludge)));
            this.inputs.put(FluidStack.class, NonNullList.withSize(1,
                    new FluidStackRecipeIngredient(new FluidStack(FluidRegistry.WATER, compressedSludge ? 900 : 100))));

            this.outputs.put(ItemStack.class, NonNullList.create());
            if (!rawOre.isEmpty())
                this.getRecipeOutputs(ItemStack.class).add(new ItemStackRecipeIngredient(rawOre));
            if (!leftOver.isEmpty())
                this.getRecipeOutputs(ItemStack.class).add(new ItemStackRecipeIngredient(leftOver));
        }

        @Override
        public int getTime()
        {
            return compressedSludge ? 90 : 10;
        }
    }
}
