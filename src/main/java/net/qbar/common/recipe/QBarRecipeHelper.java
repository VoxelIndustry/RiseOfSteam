package net.qbar.common.recipe;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.qbar.common.init.QBarItems;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;

public class QBarRecipeHelper
{
    public static void addBlockToPlateRecipe(final String metalName)
    {
        final ItemStack plate = new ItemStack(QBarItems.METALPLATE, 9);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("metal", metalName);
        plate.setTagCompound(tag);

        QBarRecipeHandler.RECIPES.get(QBarRecipeHandler.ROLLINGMILL_UID)
                .add(new RollingMillRecipe(
                        new ItemStackRecipeIngredient("block" + StringUtils.capitalize(metalName), 1),
                        new ItemStackRecipeIngredient(plate)));
    }

    public static void addIngotToPlateRecipe(final String metalName)
    {
        final ItemStack plate = new ItemStack(QBarItems.METALPLATE, 1);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("metal", metalName);
        plate.setTagCompound(tag);

        QBarRecipeHandler.RECIPES.get(QBarRecipeHandler.ROLLINGMILL_UID)
                .add(new RollingMillRecipe(
                        new ItemStackRecipeIngredient("ingot" + StringUtils.capitalize(metalName), 1),
                        new ItemStackRecipeIngredient(plate)));
    }

    public static void addLiquidBoilerRecipe(Fluid fuel, int heatPerMb, int timePerBucket)
    {
        QBarRecipeHandler.RECIPES.get(QBarRecipeHandler.LIQUIDBOILER_UID)
                .add(new LiquidBoilerRecipe(fuel, heatPerMb, timePerBucket));
    }
}
