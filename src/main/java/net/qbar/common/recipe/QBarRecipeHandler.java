package net.qbar.common.recipe;

import com.google.common.collect.ArrayListMultimap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.QBar;
import net.qbar.common.init.QBarItems;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;
import net.qbar.common.recipe.ingredient.RecipeIngredient;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Optional;

public class QBarRecipeHandler
{
    public static final String                                ROLLINGMILL_UID  = QBar.MODID + ".rollingmill";
    public static final String                                FURNACE_UID      = QBar.MODID + ".furnace";
    public static final String                                CRAFT_UID        = QBar.MODID + ".craft";
    public static final String                                LIQUIDBOILER_UID = QBar.MODID + ".liquidboiler";

    public static final ArrayListMultimap<String, QBarRecipe> RECIPES          = ArrayListMultimap.create();

    public static final ArrayList<String>                     metals           = new ArrayList<>();

    public static void registerRecipes()
    {
        QBarRecipeHandler.metals.forEach(metalName ->
        {
            QBarRecipeHandler.addIngotToPlateRecipe(metalName);
            QBarRecipeHandler.addBlockToPlateRecipe(metalName);
        });

        QBarRecipeHandler.addLiquidBoilerRecipe(FluidRegistry.LAVA, 2, 1200);

        GameRegistry.addRecipe(new SludgeRecipe());
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithoutCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        if (recipeID.equals(QBarRecipeHandler.FURNACE_UID))
            return !FurnaceRecipes.instance().getSmeltingResult((ItemStack) ingredient).isEmpty();
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
        {
            return QBarRecipeHandler.RECIPES.get(recipeID).stream().anyMatch(recipe ->
            {
                if (!recipe.hasInputType(ingredient.getClass())
                        || recipe.getRecipeInputs(ingredient.getClass()).size() < recipeSlot)
                    return false;
                return ((RecipeIngredient<T>) recipe.getRecipeInputs(ingredient.getClass()).get(recipeSlot))
                        .match(ingredient);
            });
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean inputMatchWithCount(final String recipeID, final int recipeSlot, final T ingredient)
    {
        if (recipeID.equals(QBarRecipeHandler.FURNACE_UID))
            return !FurnaceRecipes.instance().getSmeltingResult((ItemStack) ingredient).isEmpty();
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
        {
            return QBarRecipeHandler.RECIPES.get(recipeID).stream().anyMatch(recipe ->
            {
                if (!recipe.hasInputType(ingredient.getClass())
                        || recipe.getRecipeInputs(ingredient.getClass()).size() < recipeSlot)
                    return false;
                return ((RecipeIngredient<T>) recipe.getRecipeInputs(ingredient.getClass()).get(recipeSlot))
                        .matchWithQuantity(ingredient);
            });
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static Optional<QBarRecipe> getRecipe(final String recipeID, final Object... inputs)
    {
        if (recipeID.equals(QBarRecipeHandler.FURNACE_UID))
        {
            final ItemStack result = FurnaceRecipes.instance().getSmeltingResult((ItemStack) inputs[0]);
            if (!result.isEmpty())
                return Optional.of(new FurnaceRecipeWrapper(((ItemStack) inputs[0]).copy(), result));
            else
                return Optional.empty();
        }
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
        {
            return QBarRecipeHandler.RECIPES.get(recipeID).stream().filter(recipe ->
            {
                int i = 0;
                for (final Object ingredient : inputs)
                {
                    if (!recipe.hasInputType(ingredient.getClass())
                            || i >= recipe.getRecipeInputs(ingredient.getClass()).size())
                        break;
                    if (!((RecipeIngredient<Object>) recipe.getRecipeInputs(ingredient.getClass()).get(i))
                            .matchWithQuantity(ingredient))
                        return false;
                    i++;
                }
                return true;
            }).findFirst();
        }
        return Optional.empty();
    }

    private static void addBlockToPlateRecipe(final String metalName)
    {
        final ItemStack plate = new ItemStack(QBarItems.METALPLATE, 9);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("metal", metalName);
        plate.setTagCompound(tag);

        QBarRecipeHandler.RECIPES.put(QBarRecipeHandler.ROLLINGMILL_UID,
                new RollingMillRecipe(new ItemStackRecipeIngredient("block" + StringUtils.capitalize(metalName), 1),
                        new ItemStackRecipeIngredient(plate)));
    }

    private static void addIngotToPlateRecipe(final String metalName)
    {
        final ItemStack plate = new ItemStack(QBarItems.METALPLATE, 1);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("metal", metalName);
        plate.setTagCompound(tag);

        QBarRecipeHandler.RECIPES.put(QBarRecipeHandler.ROLLINGMILL_UID,
                new RollingMillRecipe(new ItemStackRecipeIngredient("ingot" + StringUtils.capitalize(metalName), 1),
                        new ItemStackRecipeIngredient(plate)));
    }

    private static void addLiquidBoilerRecipe(Fluid fuel, int heatPerMb, int timePerBucket)
    {
        QBarRecipeHandler.RECIPES.put(QBarRecipeHandler.LIQUIDBOILER_UID,
                new LiquidBoilerRecipe(fuel, heatPerMb, timePerBucket));
    }
}
