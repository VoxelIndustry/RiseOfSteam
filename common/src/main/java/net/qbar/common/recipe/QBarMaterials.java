package net.qbar.common.recipe;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Optional;

public class QBarMaterials
{
    public static final ArrayList<String> metals = new ArrayList<>();
    public static final String IRON = "iron";
    public static final String GOLD = "gold";
    public static final String COPPER = "copper";
    public static final String BRONZE = "bronze";
    public static final String BRASS = "brass";
    public static final String TIN = "tin";
    public static final String ZINC = "zinc";
    public static final String NICKEL = "nickel";
    public static final String LEAD = "lead";
    public static final String STEEL = "steel";

    private static final Table<String, MaterialShape, ItemStack> cachedMetalVariants = HashBasedTable.create();

    public static void initMaterials()
    {
        metals.add("iron");
        metals.add("gold");
        metals.add("copper");
        metals.add("bronze");
        metals.add("brass");
        metals.add("tin");
        metals.add("zinc");
        metals.add("nickel");
        metals.add("lead");
        metals.add("steel");
    }

    public static Optional<String> getMetalFromFluid(FluidStack moltenMetal)
    {
        return metals.stream().filter(metal -> metal.equals(moltenMetal.getFluid().getName().replace("molten", "")))
                .findAny();
    }

    public static ItemStack getPlateFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, MaterialShape.PLATE))
            cachedMetalVariants.put(metal, MaterialShape.PLATE,
                    OreDictionary.getOres("plate" + StringUtils.capitalize(metal)).get(0));
        return cachedMetalVariants.get(metal, MaterialShape.PLATE);
    }

    public static ItemStack getGearFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, MaterialShape.GEAR))
            cachedMetalVariants.put(metal, MaterialShape.GEAR,
                    OreDictionary.getOres("gear" + StringUtils.capitalize(metal)).get(0));
        return cachedMetalVariants.get(metal, MaterialShape.GEAR);
    }

    public static ItemStack getIngotFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, MaterialShape.INGOT))
            cachedMetalVariants.put(metal, MaterialShape.INGOT,
                    OreDictionary.getOres("ingot" + StringUtils.capitalize(metal)).get(0));
        return cachedMetalVariants.get(metal, MaterialShape.INGOT);
    }

    public static ItemStack getBlockFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, MaterialShape.BLOCK))
            cachedMetalVariants.put(metal, MaterialShape.BLOCK,
                    OreDictionary.getOres("block" + StringUtils.capitalize(metal)).get(0));
        return cachedMetalVariants.get(metal, MaterialShape.BLOCK);
    }

    public static Fluid getFluidFromMetal(String metal)
    {
        return FluidRegistry.getFluid("molten" + metal);
    }

    public enum MaterialShape
    {
        INGOT, GEAR, PLATE, BLOCK
    }
}
