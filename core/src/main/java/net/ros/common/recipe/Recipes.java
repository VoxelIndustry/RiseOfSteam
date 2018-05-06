package net.ros.common.recipe;

import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.ros.common.ROSConstants;
import net.ros.common.block.BlockMetal;
import net.ros.common.init.ROSBlocks;
import net.ros.common.init.ROSItems;
import net.ros.common.ore.MineralDensity;
import net.ros.common.ore.Ores;
import net.ros.common.recipe.category.FurnaceRecipeCategory;
import net.ros.common.recipe.category.OreWasherRecipeCategory;
import net.ros.common.recipe.category.RecipeCategory;
import net.ros.common.recipe.category.SortingMachineRecipeCategory;
import net.ros.common.recipe.type.CapsuleRecipe;
import net.ros.common.recipe.type.SludgeRecipe;
import org.apache.commons.lang3.StringUtils;

public class Recipes
{
    public void registerRecipes()
    {
        RecipeHandler.RECIPES.put(RecipeHandler.ROLLINGMILL_UID,
                new RecipeCategory(RecipeHandler.ROLLINGMILL_UID));
        RecipeHandler.RECIPES.put(RecipeHandler.LIQUIDBOILER_UID,
                new RecipeCategory(RecipeHandler.LIQUIDBOILER_UID));
        RecipeHandler.RECIPES.put(RecipeHandler.ORE_WASHER_UID,
                new OreWasherRecipeCategory(RecipeHandler.ORE_WASHER_UID));
        RecipeHandler.RECIPES.put(RecipeHandler.FURNACE_UID,
                new FurnaceRecipeCategory(RecipeHandler.FURNACE_UID));
        RecipeHandler.RECIPES.put(RecipeHandler.SORTING_MACHINE_UID,
                new SortingMachineRecipeCategory(RecipeHandler.SORTING_MACHINE_UID));
        RecipeHandler.RECIPES.put(RecipeHandler.SAW_MILL_UID,
                new RecipeCategory(RecipeHandler.SAW_MILL_UID));
        RecipeHandler.RECIPES.put(RecipeHandler.MELTING_UID,
                new RecipeCategory(RecipeHandler.MELTING_UID));
        RecipeHandler.RECIPES.put(RecipeHandler.ALLOY_UID,
                new RecipeCategory(RecipeHandler.ALLOY_UID));

        Materials.metals.stream().forEach(metalName ->
        {
            if (Materials.metals.containsShape(metalName, MaterialShape.PLATE))
            {
                RecipeHelper.addIngotToPlateRecipe(metalName);
                RecipeHelper.addBlockToPlateRecipe(metalName);
            }

            if (Materials.metals.containsShape(metalName, MaterialShape.BLOCK))
            {
                RecipeHelper.addBlockToIngotRecipe(metalName);
                RecipeHelper.addIngotToBlockRecipe(metalName);
            }
            if (Materials.metals.containsShape(metalName, MaterialShape.NUGGET))
            {
                RecipeHelper.addNuggetToIngotRecipe(metalName);
                RecipeHelper.addIngotToNuggetRecipe(metalName);
            }
            if (Materials.metals.containsShape(metalName, MaterialShape.GEAR))
                RecipeHelper.addIngotToGearRecipe(metalName);
        });

        Ores.MINERALS.stream().filter(mineral -> mineral != Ores.REDSTONE).forEach
                (RecipeHelper::addRawOreFurnaceRecipe);
        FurnaceRecipes.instance().addSmeltingRecipe(Ores.getRawMineral(Ores.REDSTONE, MineralDensity.POOR),
                new ItemStack(Items.REDSTONE, 3), 0.25f);
        FurnaceRecipes.instance().addSmeltingRecipe(Ores.getRawMineral(Ores.REDSTONE, MineralDensity.NORMAL),
                new ItemStack(Items.REDSTONE, 6), 0.5f);
        FurnaceRecipes.instance().addSmeltingRecipe(Ores.getRawMineral(Ores.REDSTONE, MineralDensity.RICH),
                new ItemStack(Items.REDSTONE, 12), 1f);

        RecipeHelper.addLiquidBoilerRecipe(FluidRegistry.LAVA, 2, 1200);

        RecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata()),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.OAK.getMetadata()));
        RecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.SPRUCE.getMetadata()));
        RecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata()),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.BIRCH.getMetadata()));
        RecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.JUNGLE.getMetadata()));
        RecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG2, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.ACACIA.getMetadata()));
        RecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG2, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() -
                        4),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.DARK_OAK.getMetadata()));

        RecipeHandler.CRAFTING_RECIPES.add(new SludgeRecipe().setRegistryName(
                new ResourceLocation(ROSConstants.MODID, "compressedsludge")));
        RecipeHandler.CRAFTING_RECIPES.add(new CapsuleRecipe().setRegistryName(
                new ResourceLocation(ROSConstants.MODID, "steamcapsule")));

        RecipeHelper.addMeltingRecipe("iron", 1204, 1204 * 1.25f, 35);
        RecipeHelper.addMeltingRecipe("gold", 1064, 1064 * 1.25f, 20);
        RecipeHelper.addMeltingRecipe("copper", 1085, 1085 * 1.25f, 30);
        RecipeHelper.addMeltingRecipe("bronze", 950, 950 * 1.25f, 25);
        RecipeHelper.addMeltingRecipe("brass", 927, 927 * 1.25f, 25);
        RecipeHelper.addMeltingRecipe("tin", 232, 232 * 1.25f, 10);
        RecipeHelper.addMeltingRecipe("zinc", 419, 419 * 1.25f, 15);
        RecipeHelper.addMeltingRecipe("nickel", 1455, 1455 * 1.25f, 40);
        RecipeHelper.addMeltingRecipe("lead", 327, 327 * 1.25f, 15);
        RecipeHelper.addMeltingRecipe("steel", 1371, 1371 * 1.25f, 35);

        RecipeHelper.addAlloyRecipe("tin", "copper", 3, "bronze");
        RecipeHelper.addAlloyRecipe("zinc", "copper", 3, "brass");
    }

    public void registerOreDict()
    {
        ROSItems.METALGEAR.getMetals().forEach(metal ->
        {
            ItemStack gear = new ItemStack(ROSItems.METALGEAR, 1, Materials.metals.indexOf(metal));
            OreDictionary.registerOre("gear" + StringUtils.capitalize(metal), gear);
        });

        ROSItems.METALPLATE.getMetals().forEach(metal ->
        {
            ItemStack plate = new ItemStack(ROSItems.METALPLATE, 1, Materials.metals.indexOf(metal));
            OreDictionary.registerOre("plate" + StringUtils.capitalize(metal), plate);
        });

        ROSItems.METALINGOT.getMetals().forEach(metal ->
        {
            ItemStack ingot = new ItemStack(ROSItems.METALINGOT, 1, Materials.metals.indexOf(metal));
            OreDictionary.registerOre("ingot" + StringUtils.capitalize(metal), ingot);
        });

        ROSItems.METALNUGGET.getMetals().forEach(metal ->
        {
            ItemStack nugget = new ItemStack(ROSItems.METALNUGGET, 1, Materials.metals.indexOf(metal));
            OreDictionary.registerOre("nugget" + StringUtils.capitalize(metal), nugget);
        });

        BlockMetal.VARIANTS.getAllowedValues().forEach(metal ->
                OreDictionary.registerOre("block" + StringUtils.capitalize(metal),
                        new ItemStack(ROSBlocks.METALBLOCK, 1, Materials.metals.indexOf(metal))));
    }

    @SubscribeEvent
    public void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        this.registerOreDict();
        this.registerRecipes();

        event.getRegistry().registerAll(RecipeHandler.CRAFTING_RECIPES.toArray(
                new IRecipe[RecipeHandler.CRAFTING_RECIPES.size()]));
    }
}
