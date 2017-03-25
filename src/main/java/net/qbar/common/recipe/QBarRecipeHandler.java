package net.qbar.common.recipe;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.QBar;
import net.qbar.common.init.QBarItems;

public class QBarRecipeHandler
{
    public static final String                                ROLLINGMILL_UID = QBar.MODID + ".rollingmill";
    public static final String                                FURNACE_UID     = QBar.MODID + ".furnace";
    public static final String                                CRAFT_UID       = QBar.MODID + ".craft";

    public static final ArrayListMultimap<String, QBarRecipe> RECIPES         = ArrayListMultimap.create();

    public static final ArrayList<String>                     metals          = new ArrayList<>();

    public static void registerRecipes()
    {
        QBarRecipeHandler.metals.forEach(metalName ->
        {
            QBarRecipeHandler.addIngotToPlateRecipe(metalName);
            QBarRecipeHandler.addBlockToPlateRecipe(metalName);
        });
    }

    public static boolean inputMatchWithoutCount(final String recipeID, final int recipeSlot, final ItemStack stack)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
        {
            return QBarRecipeHandler.RECIPES.get(recipeID).stream().anyMatch(recipe ->
            {
                if (recipe.getRecipeInputs(ItemStack.class).size() < recipeSlot)
                    return false;
                return recipe.getRecipeInputs(ItemStack.class).get(recipeSlot).match(stack);
            });
        }
        return false;
    }

    public static boolean inputMatchWithCount(final String recipeID, final int recipeSlot, final ItemStack stack)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
        {
            return QBarRecipeHandler.RECIPES.get(recipeID).stream().anyMatch(recipe ->
            {
                if (recipe.getRecipeInputs(ItemStack.class).size() < recipeSlot)
                    return false;
                return recipe.getRecipeInputs(ItemStack.class).get(recipeSlot).matchWithQuantity(stack);
            });
        }
        return false;
    }

    public static Optional<QBarRecipe> getRecipe(final String recipeID, final ItemStack... inputs)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
        {
            return QBarRecipeHandler.RECIPES.get(recipeID).stream().filter(recipe ->
            {
                int i = 0;
                for (final ItemStack stack : inputs)
                {
                    if (i >= recipe.getRecipeInputs(ItemStack.class).size())
                        break;
                    if (!recipe.getRecipeInputs(ItemStack.class).get(i).matchWithQuantity(stack))
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
}
