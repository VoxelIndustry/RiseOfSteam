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
import net.ros.common.init.ROSBlocks;
import net.ros.common.init.ROSItems;
import net.ros.common.ore.MineralDensity;
import net.ros.common.ore.Ores;
import net.ros.common.recipe.category.FurnaceRecipeCategory;
import net.ros.common.recipe.category.RecipeCategory;
import net.ros.common.recipe.category.SortingMachineRecipeCategory;
import net.ros.common.recipe.type.CapsuleRecipe;
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
                new RecipeCategory(RecipeHandler.ORE_WASHER_UID));
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

        Materials.metals.stream().forEach(metal ->
        {
            if (Materials.metals.containsShape(metal, MaterialShape.PLATE))
            {
                RecipeHelper.addIngotToPlateRecipe(metal);
                RecipeHelper.addBlockToPlateRecipe(metal);
            }

            if (Materials.metals.containsShape(metal, MaterialShape.BLOCK))
            {
                RecipeHelper.addBlockToIngotRecipe(metal);
                RecipeHelper.addIngotToBlockRecipe(metal);
            }
            if (Materials.metals.containsShape(metal, MaterialShape.NUGGET))
            {
                RecipeHelper.addNuggetToIngotRecipe(metal);
                RecipeHelper.addIngotToNuggetRecipe(metal);
            }
            if (Materials.metals.containsShape(metal, MaterialShape.GEAR))
                RecipeHelper.addIngotToGearRecipe(metal);
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

        RecipeHandler.CRAFTING_RECIPES.add(new CapsuleRecipe().setRegistryName(
                new ResourceLocation(ROSConstants.MODID, "steamcapsule")));

        RecipeHelper.addMeltingRecipe(Materials.IRON, 35);
        RecipeHelper.addMeltingRecipe(Materials.GOLD, 20);
        RecipeHelper.addMeltingRecipe(Materials.COPPER, 30);
        RecipeHelper.addMeltingRecipe(Materials.BRONZE, 25);
        RecipeHelper.addMeltingRecipe(Materials.BRASS, 25);
        RecipeHelper.addMeltingRecipe(Materials.TIN, 10);
        RecipeHelper.addMeltingRecipe(Materials.ZINC, 15);
        RecipeHelper.addMeltingRecipe(Materials.NICKEL, 40);
        RecipeHelper.addMeltingRecipe(Materials.LEAD, 15);
        RecipeHelper.addMeltingRecipe(Materials.STEEL, 35);

        RecipeHelper.addAlloyRecipe(Materials.TIN, Materials.COPPER, 3, Materials.BRONZE);
        RecipeHelper.addAlloyRecipe(Materials.ZINC, Materials.COPPER, 3, Materials.BRASS);

        Ores.ORES.forEach(ore -> RecipeHelper.addOreWashingRecipe(ore, FluidRegistry.WATER, 0));
    }

    public void registerOreDict()
    {
        ROSItems.METALGEAR.getMetals().forEach(metal ->
        {
            ItemStack gear = new ItemStack(ROSItems.METALGEAR, 1, Materials.metals.indexOf(metal));
            OreDictionary.registerOre("gear" + StringUtils.capitalize(metal.getName()), gear);
        });

        ROSItems.METALPLATE.getMetals().forEach(metal ->
        {
            ItemStack plate = new ItemStack(ROSItems.METALPLATE, 1, Materials.metals.indexOf(metal));
            OreDictionary.registerOre("plate" + StringUtils.capitalize(metal.getName()), plate);
        });

        ROSItems.METALINGOT.getMetals().forEach(metal ->
        {
            ItemStack ingot = new ItemStack(ROSItems.METALINGOT, 1, Materials.metals.indexOf(metal));
            OreDictionary.registerOre("ingot" + StringUtils.capitalize(metal.getName()), ingot);
        });

        ROSItems.METALNUGGET.getMetals().forEach(metal ->
        {
            ItemStack nugget = new ItemStack(ROSItems.METALNUGGET, 1, Materials.metals.indexOf(metal));
            OreDictionary.registerOre("nugget" + StringUtils.capitalize(metal.getName()), nugget);
        });


        ROSBlocks.METALBLOCK.getVariants().getAllowedValues().forEach(metalName ->
                OreDictionary.registerOre("block" + StringUtils.capitalize(metalName),
                        new ItemStack(ROSBlocks.METALBLOCK, 1,
                                Materials.metals.indexOf(Materials.metals.byName(metalName).get()))));
    }

    @SubscribeEvent
    public void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        this.registerOreDict();
        this.registerRecipes();

        event.getRegistry().registerAll(RecipeHandler.CRAFTING_RECIPES.toArray(new IRecipe[0]));
    }
}
