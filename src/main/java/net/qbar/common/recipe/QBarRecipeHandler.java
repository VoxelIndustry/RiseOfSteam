package net.qbar.common.recipe;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import net.qbar.QBar;
import net.qbar.common.init.QBarItems;

public class QBarRecipeHandler
{
    public static final String                       ROLLINGMILL_UID = QBar.MODID + ".rollingmill";

    public static final ArrayList<RollingMillRecipe> ROLLINGMILL     = new ArrayList<>();

    public static void registerRecipes()
    {
        QBarRecipeHandler.metals.forEach(metalName ->
        {
            QBarRecipeHandler.addIngotToPlateRecipe(metalName);
            QBarRecipeHandler.addBlockToPlateRecipe(metalName);
        });
    }

    private static void addBlockToPlateRecipe(final String metalName)
    {
        final ItemStack metal = OreDictionary.getOres("block" + StringUtils.capitalize(metalName)).get(0).copy();
        metal.setCount(1);

        final ItemStack plate = new ItemStack(QBarItems.METALPLATE, 9);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("metal", metalName);
        plate.setTagCompound(tag);

        QBarRecipeHandler.ROLLINGMILL.add(new RollingMillRecipe(metal, plate));
    }

    private static void addIngotToPlateRecipe(final String metalName)
    {
        final ItemStack metal = OreDictionary.getOres("ingot" + StringUtils.capitalize(metalName)).get(0).copy();
        metal.setCount(1);
        final ItemStack plate = new ItemStack(QBarItems.METALPLATE, 1);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("metal", metalName);
        plate.setTagCompound(tag);

        QBarRecipeHandler.ROLLINGMILL.add(new RollingMillRecipe(metal, plate));
    }

    public static final ArrayList<String> metals = new ArrayList<>();
}
