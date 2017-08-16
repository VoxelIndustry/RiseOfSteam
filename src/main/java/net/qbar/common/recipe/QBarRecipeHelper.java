package net.qbar.common.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.qbar.QBar;
import net.qbar.common.init.QBarItems;
import net.qbar.common.recipe.ingredient.ItemStackRecipeIngredient;
import org.apache.commons.lang3.StringUtils;

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

    public static void addIngotToGearRecipe(String metalName)
    {
        ItemStack gearStack = new ItemStack(QBarItems.METALGEAR);
        gearStack.setTagCompound(new NBTTagCompound());
        gearStack.getTagCompound().setString("metal", metalName);

        QBarRecipeHandler.CRAFTING_RECIPES.add(new ShapedOreRecipe(new ResourceLocation(QBar.MODID, "gear" + metalName),
                gearStack, " X ", "XOX", " X ",
                'X', new OreIngredient("ingot" + StringUtils.capitalize(metalName)), 'O', new ItemStack(Items.IRON_INGOT))
                .setRegistryName(new ResourceLocation(QBar.MODID, "gear" + metalName)));
    }

    public static void addLiquidBoilerRecipe(Fluid fuel, int heatPerMb, int timePerBucket)
    {
        QBarRecipeHandler.RECIPES.get(QBarRecipeHandler.LIQUIDBOILER_UID)
                .add(new LiquidBoilerRecipe(fuel, heatPerMb, timePerBucket));
    }
}
