package net.qbar.common.recipe;

import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.qbar.QBar;
import net.qbar.common.QBarConstants;
import net.qbar.common.block.BlockMetal;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.recipe.category.FurnaceRecipeCategory;
import net.qbar.common.recipe.category.OreWasherRecipeCategory;
import net.qbar.common.recipe.category.QBarRecipeCategory;
import net.qbar.common.recipe.category.SortingMachineRecipeCategory;
import net.qbar.common.recipe.type.SludgeRecipe;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class QBarRecipeHandler
{
    public static final String ROLLINGMILL_UID     = QBarConstants.MODID + ".rollingmill";
    public static final String FURNACE_UID         = QBarConstants.MODID + ".furnace";
    public static final String LIQUIDBOILER_UID    = QBarConstants.MODID + ".liquidboiler";
    public static final String ORE_WASHER_UID      = QBarConstants.MODID + ".orewasher";
    public static final String SORTING_MACHINE_UID = QBarConstants.MODID + ".sortingmachine";
    public static final String SAW_MILL_UID        = QBarConstants.MODID + ".sawmill";

    public static final HashMap<String, QBarRecipeCategory> RECIPES = new HashMap<>();

    public static final ArrayList<IRecipe> CRAFTING_RECIPES = new ArrayList<>();

    public static void registerRecipes()
    {
        QBarRecipeHandler.RECIPES.put(ROLLINGMILL_UID, new QBarRecipeCategory(ROLLINGMILL_UID));
        QBarRecipeHandler.RECIPES.put(LIQUIDBOILER_UID, new QBarRecipeCategory(LIQUIDBOILER_UID));
        QBarRecipeHandler.RECIPES.put(ORE_WASHER_UID, new OreWasherRecipeCategory(ORE_WASHER_UID));
        QBarRecipeHandler.RECIPES.put(FURNACE_UID, new FurnaceRecipeCategory(FURNACE_UID));
        QBarRecipeHandler.RECIPES.put(SORTING_MACHINE_UID, new SortingMachineRecipeCategory(SORTING_MACHINE_UID));
        QBarRecipeHandler.RECIPES.put(SAW_MILL_UID, new QBarRecipeCategory(SAW_MILL_UID));

        QBarMaterials.metals.forEach(metalName ->
        {
            QBarRecipeHelper.addIngotToPlateRecipe(metalName);
            QBarRecipeHelper.addBlockToPlateRecipe(metalName);

            if (BlockMetal.VARIANTS.getAllowedValues().contains(metalName))
            {
                QBarRecipeHelper.addBlockToIngotRecipe(metalName);
                QBarRecipeHelper.addIngotToBlockRecipe(metalName);
            }
            if (QBarItems.METALGEAR.hasMetalVariant(metalName))
                QBarRecipeHelper.addIngotToGearRecipe(metalName);
        });

        QBarRecipeHelper.addLiquidBoilerRecipe(FluidRegistry.LAVA, 2, 1200);

        QBarRecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata()),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.OAK.getMetadata()));
        QBarRecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.SPRUCE.getMetadata()));
        QBarRecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata()),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.BIRCH.getMetadata()));
        QBarRecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.JUNGLE.getMetadata()));
        QBarRecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG2, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.ACACIA.getMetadata()));
        QBarRecipeHelper.addSawMillRecipe(new ItemStack(Blocks.LOG2, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4),
                new ItemStack(Blocks.PLANKS, 5, BlockPlanks.EnumType.DARK_OAK.getMetadata()));

        CRAFTING_RECIPES.add(new SludgeRecipe().setRegistryName(new ResourceLocation(QBarConstants.MODID, "compressedsludge")));
    }

    public static void registerOreDict()
    {
        QBarItems.METALGEAR.getMetals().forEach(metal -> {

            ItemStack gear = new ItemStack(QBarItems.METALGEAR, 1, QBarMaterials.metals.indexOf(metal));
            OreDictionary.registerOre("gear" + StringUtils.capitalize(metal), gear);
        });

        QBarItems.METALPLATE.getMetals().forEach(metal -> {

            ItemStack gear = new ItemStack(QBarItems.METALPLATE, 1, QBarMaterials.metals.indexOf(metal));
            OreDictionary.registerOre("plate" + StringUtils.capitalize(metal), gear);
        });

        QBarItems.METALINGOT.getMetals().forEach(metal -> {

            ItemStack gear = new ItemStack(QBarItems.METALINGOT, 1, QBarMaterials.metals.indexOf(metal));
            OreDictionary.registerOre("ingot" + StringUtils.capitalize(metal), gear);
        });

        BlockMetal.VARIANTS.getAllowedValues().forEach(metal -> OreDictionary.registerOre("block" + StringUtils.capitalize(metal),
                new ItemStack(QBarBlocks.METALBLOCK, 1, QBarMaterials.metals.indexOf(metal))));
    }

    @SubscribeEvent
    public void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        QBarRecipeHandler.registerOreDict();
        QBarRecipeHandler.registerRecipes();

        event.getRegistry().registerAll(CRAFTING_RECIPES.toArray(new IRecipe[CRAFTING_RECIPES.size()]));
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithoutCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
            return QBarRecipeHandler.RECIPES.get(recipeID).inputMatchWithoutCount(recipeSlot, ingredient);
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
            return QBarRecipeHandler.RECIPES.get(recipeID).inputMatchWithCount(recipeSlot, ingredient);
        return false;
    }

    @SuppressWarnings("unchecked")
    public static Optional<QBarRecipe> getRecipe(final String recipeID, final Object... inputs)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
            return QBarRecipeHandler.RECIPES.get(recipeID).getRecipe(inputs);
        return Optional.empty();
    }
}
