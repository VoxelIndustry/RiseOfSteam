package net.ros.common.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.ros.common.ROSConstants;
import net.ros.common.init.ROSBlocks;
import net.ros.common.init.ROSItems;
import net.ros.common.ore.Mineral;
import net.ros.common.ore.MineralDensity;
import net.ros.common.ore.Ores;
import net.ros.common.recipe.ingredient.FluidStackRecipeIngredient;
import net.ros.common.recipe.ingredient.ItemStackRecipeIngredient;
import net.ros.common.recipe.type.*;
import org.apache.commons.lang3.StringUtils;

public class RecipeHelper
{
    public static void addBlockToPlateRecipe(Metal metal)
    {
        final ItemStack plate = new ItemStack(ROSItems.METALPLATE, 9, Materials.metals.indexOf(metal));

        RecipeHandler.RECIPES.get(RecipeHandler.ROLLINGMILL_UID)
                .add(new RollingMillRecipe(
                        new ItemStackRecipeIngredient("block" + StringUtils.capitalize(metal.getName()), 1),
                        new ItemStackRecipeIngredient(plate)));
    }

    public static void addIngotToPlateRecipe(Metal metal)
    {
        final ItemStack plate = new ItemStack(ROSItems.METALPLATE, 1, Materials.metals.indexOf(metal));

        RecipeHandler.RECIPES.get(RecipeHandler.ROLLINGMILL_UID)
                .add(new RollingMillRecipe(
                        new ItemStackRecipeIngredient("ingot" + StringUtils.capitalize(metal.getName()), 1),
                        new ItemStackRecipeIngredient(plate)));
    }

    public static void addIngotToGearRecipe(Metal metal)
    {
        ItemStack gearStack = new ItemStack(ROSItems.METALGEAR, 1, Materials.metals.indexOf(metal));

        RecipeHandler.CRAFTING_RECIPES.add(new ShapedOreRecipe(new ResourceLocation(ROSConstants.MODID, "gear" +
                metal), gearStack, " X ", "XOX", " X ", 'X',
                new OreIngredient("ingot" + StringUtils.capitalize(metal.getName())), 'O',
                new ItemStack(Items.IRON_INGOT))
                .setRegistryName(new ResourceLocation(ROSConstants.MODID, "gear" + metal)));
    }

    public static void addBlockToIngotRecipe(Metal metal)
    {
        ItemStack ingotStack = new ItemStack(ROSItems.METALINGOT, 9, Materials.metals.indexOf(metal));

        RecipeHandler.CRAFTING_RECIPES
                .add(new ShapelessOreRecipe(new ResourceLocation(ROSConstants.MODID, "block" + metal), ingotStack,
                        new OreIngredient("block" + StringUtils.capitalize(metal.getName())))
                        .setRegistryName(new ResourceLocation(ROSConstants.MODID, "block" + metal)));
    }

    public static void addIngotToBlockRecipe(Metal metal)
    {
        ItemStack blockStack = new ItemStack(ROSBlocks.METALBLOCK, 1, Materials.metals.indexOf(metal));

        RecipeHandler.CRAFTING_RECIPES
                .add(new ShapedOreRecipe(new ResourceLocation(ROSConstants.MODID, "block_ingot" + metal),
                        blockStack, "XXX", "XXX", "XXX", 'X',
                        new OreIngredient("ingot" + StringUtils.capitalize(metal.getName())))
                        .setRegistryName(new ResourceLocation(ROSConstants.MODID, "block_ingot" + metal)));
    }

    public static void addIngotToNuggetRecipe(Metal metal)
    {
        ItemStack ingotStack = new ItemStack(ROSItems.METALINGOT, 1, Materials.metals.indexOf(metal));

        RecipeHandler.CRAFTING_RECIPES
                .add(new ShapedOreRecipe(new ResourceLocation(ROSConstants.MODID, "nugget_ingot" + metal),
                        ingotStack, "XXX", "XXX", "XXX", 'X',
                        new OreIngredient("nugget" + StringUtils.capitalize(metal.getName())))
                        .setRegistryName(new ResourceLocation(ROSConstants.MODID, "nugget_ingot" + metal)));
    }

    public static void addNuggetToIngotRecipe(Metal metal)
    {
        ItemStack nuggetStack = new ItemStack(ROSItems.METALNUGGET, 9, Materials.metals.indexOf(metal));

        RecipeHandler.CRAFTING_RECIPES
                .add(new ShapelessOreRecipe(new ResourceLocation(ROSConstants.MODID, "ingot_nugget" + metal),
                        nuggetStack, new OreIngredient("ingot" + StringUtils.capitalize(metal.getName())))
                        .setRegistryName(new ResourceLocation(ROSConstants.MODID, "ingot_nugget" + metal)));
    }

    public static void addRawOreFurnaceRecipe(Mineral mineral)
    {
        ItemStack poorOre = Ores.getRawMineral(mineral, MineralDensity.POOR);
        ItemStack normalOre = Ores.getRawMineral(mineral, MineralDensity.NORMAL);
        ItemStack richOre = Ores.getRawMineral(mineral, MineralDensity.RICH);

        ItemStack nuggetStack = OreDictionary.getOres("nugget" + StringUtils.capitalize(mineral.getNameID())).get(0)
                .copy();
        nuggetStack.setCount(4);

        ItemStack ingotStack = OreDictionary.getOres("ingot" + StringUtils.capitalize(mineral.getNameID())).get(0)
                .copy();
        ingotStack.setCount(1);

        FurnaceRecipes.instance().addSmeltingRecipe(poorOre, nuggetStack,
                0.25f + (0.25f * mineral.getRarity().ordinal()));

        FurnaceRecipes.instance().addSmeltingRecipe(normalOre, ingotStack,
                0.5f + (0.5f * mineral.getRarity().ordinal()));

        ingotStack = ingotStack.copy();
        ingotStack.setCount(2);
        FurnaceRecipes.instance().addSmeltingRecipe(richOre, ingotStack,
                1f + mineral.getRarity().ordinal());
    }

    public static void addLiquidBoilerRecipe(Fluid fuel, int heatPerMb, int timePerBucket)
    {
        RecipeHandler.RECIPES.get(RecipeHandler.LIQUIDBOILER_UID)
                .add(new LiquidBoilerRecipe(fuel, heatPerMb, timePerBucket));
    }

    public static void addSawMillRecipe(ItemStack input, ItemStack output)
    {
        RecipeHandler.RECIPES.get(RecipeHandler.SAW_MILL_UID).add(
                new SawMillRecipe(new ItemStackRecipeIngredient(input), new ItemStackRecipeIngredient(output)));
    }

    public static void addMeltingRecipe(Metal metal, int
            baseMeltingTime)
    {
        RecipeHandler.RECIPES.get(RecipeHandler.MELTING_UID).add(
                new MeltRecipe(new ItemStackRecipeIngredient("nugget" + StringUtils.capitalize(metal.getName()), 1),
                        new FluidStackRecipeIngredient(Materials.getFluidStackFromMetal(metal, 16)),
                        metal.getMeltingPoint(), metal.getMeltingPoint() * 1.25f, baseMeltingTime)
        );

        RecipeHandler.RECIPES.get(RecipeHandler.MELTING_UID).add(
                new MeltRecipe(new ItemStackRecipeIngredient("ingot" + StringUtils.capitalize(metal.getName()), 1),
                        new FluidStackRecipeIngredient(Materials.getFluidStackFromMetal(metal, 144)),
                        metal.getMeltingPoint(), metal.getMeltingPoint() * 1.25f, baseMeltingTime)
        );

        RecipeHandler.RECIPES.get(RecipeHandler.MELTING_UID).add(
                new MeltRecipe(new ItemStackRecipeIngredient("block" + StringUtils.capitalize(metal.getName()), 1),
                        new FluidStackRecipeIngredient(Materials.getFluidStackFromMetal(metal, 1296)),
                        metal.getMeltingPoint(), metal.getMeltingPoint() * 1.25f, baseMeltingTime))
        ;

        RecipeHandler.RECIPES.get(RecipeHandler.MELTING_UID).add(
                new MeltRecipe(new ItemStackRecipeIngredient("plate" + StringUtils.capitalize(metal.getName()), 1),
                        new FluidStackRecipeIngredient(Materials.getFluidStackFromMetal(metal, 144)),
                        metal.getMeltingPoint(), metal.getMeltingPoint() * 1.25f, baseMeltingTime)
        );
    }

    public static void addAlloyRecipe(Metal first, Metal second, int secondCount, Metal resultMetal)
    {
        RecipeHandler.RECIPES.get(RecipeHandler.ALLOY_UID).add(
                new AlloyRecipe(new FluidStackRecipeIngredient(Materials.getFluidStackFromMetal(first, 1)),
                        new FluidStackRecipeIngredient(Materials.getFluidStackFromMetal(second, secondCount)),
                        new FluidStackRecipeIngredient(Materials.getFluidStackFromMetal(resultMetal, 1 + secondCount)))
        );
    }
}
