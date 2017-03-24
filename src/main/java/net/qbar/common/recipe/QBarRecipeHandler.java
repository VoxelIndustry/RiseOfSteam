package net.qbar.common.recipe;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import net.qbar.QBar;
import net.qbar.common.init.QBarItems;
import net.qbar.common.util.ItemUtils;

public class QBarRecipeHandler
{
    public static final String                                ROLLINGMILL_UID = QBar.MODID + ".rollingmill";

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

    public static boolean inputMatch(final String recipeID, final int recipeSlot, final ItemStack stack)
    {
        if (QBarRecipeHandler.RECIPES.containsKey(recipeID))
        {
            return QBarRecipeHandler.RECIPES.get(recipeID).stream().anyMatch(recipe ->
            {
                if (recipe.getRecipeInputs().size() < recipeSlot)
                    return false;
                return ItemUtils.deepEquals(stack, recipe.getRecipeInputs().get(recipeSlot));
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
                    if (i >= recipe.getRecipeInputs().size())
                        break;
                    if (stack.getCount() < recipe.getRecipeInputs().get(i).getCount()
                            || !ItemUtils.deepEquals(stack, recipe.getRecipeInputs().get(i)))
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
        final ItemStack metal = OreDictionary.getOres("block" + StringUtils.capitalize(metalName)).get(0).copy();
        metal.setCount(1);

        final ItemStack plate = new ItemStack(QBarItems.METALPLATE, 9);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("metal", metalName);
        plate.setTagCompound(tag);

        QBarRecipeHandler.RECIPES.put(QBarRecipeHandler.ROLLINGMILL_UID, new RollingMillRecipe(metal, plate));
    }

    private static void addIngotToPlateRecipe(final String metalName)
    {
        final ItemStack metal = OreDictionary.getOres("ingot" + StringUtils.capitalize(metalName)).get(0).copy();
        metal.setCount(1);
        final ItemStack plate = new ItemStack(QBarItems.METALPLATE, 1);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("metal", metalName);
        plate.setTagCompound(tag);

        QBarRecipeHandler.RECIPES.put(QBarRecipeHandler.ROLLINGMILL_UID, new RollingMillRecipe(metal, plate));
    }
}
