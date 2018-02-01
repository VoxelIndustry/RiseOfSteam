package net.qbar.common.recipe;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static net.qbar.common.recipe.MaterialShape.*;

public class QBarMaterials
{
    public static final String IRON   = "iron";
    public static final String GOLD   = "gold";
    public static final String COPPER = "copper";
    public static final String BRONZE = "bronze";
    public static final String BRASS  = "brass";
    public static final String TIN    = "tin";
    public static final String ZINC   = "zinc";
    public static final String NICKEL = "nickel";
    public static final String LEAD   = "lead";
    public static final String STEEL  = "steel";

    public static MetalList metals = new MetalList();

    private static final Table<String, MaterialShape, ItemStack> cachedMetalVariants = HashBasedTable.create();

    public static void initMaterials()
    {
        metals.addMetal(IRON).shapes(PLATE, GEAR);
        metals.addMetal(GOLD).shapes(PLATE);
        metals.addMetal(COPPER).shapes(NUGGET, PLATE, GEAR, INGOT, BLOCK);
        metals.addMetal(BRONZE).shapes(NUGGET, PLATE, GEAR, INGOT, BLOCK);
        metals.addMetal(BRASS).shapes(NUGGET, PLATE, GEAR, INGOT, BLOCK);
        metals.addMetal(TIN).shapes(NUGGET, PLATE, INGOT, BLOCK);
        metals.addMetal(ZINC).shapes(NUGGET, PLATE, INGOT, BLOCK);
        metals.addMetal(NICKEL).shapes(NUGGET, PLATE, INGOT, BLOCK);
        metals.addMetal(LEAD).shapes(NUGGET, PLATE, INGOT, BLOCK);
        metals.addMetal(STEEL).shapes(NUGGET, PLATE, GEAR, INGOT, BLOCK);
    }

    public static Optional<String> getMetalFromFluid(FluidStack moltenMetal)
    {
        return metals.stream()
                .filter(metal -> metal.equals(moltenMetal.getFluid().getName().replace("molten", "")))
                .findAny();
    }

    public static ItemStack getPlateFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, PLATE))
        {
            ItemStack stack = OreDictionary.getOres("plate" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, PLATE, stack);
        }
        return cachedMetalVariants.get(metal, PLATE);
    }

    public static ItemStack getGearFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, GEAR))
        {
            ItemStack stack = OreDictionary.getOres("gear" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, GEAR, stack);
        }
        return cachedMetalVariants.get(metal, GEAR);
    }

    public static ItemStack getIngotFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, INGOT))
        {
            ItemStack stack = OreDictionary.getOres("ingot" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, INGOT, stack);
        }
        return cachedMetalVariants.get(metal, INGOT);
    }

    public static ItemStack getBlockFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, BLOCK))
        {
            ItemStack stack = OreDictionary.getOres("block" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, BLOCK, stack);
        }
        return cachedMetalVariants.get(metal, BLOCK);
    }

    public static ItemStack getNuggetFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, NUGGET))
        {
            ItemStack stack = OreDictionary.getOres("nugget" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, NUGGET, stack);
        }
        return cachedMetalVariants.get(metal, NUGGET);
    }

    public static Fluid getFluidFromMetal(String metal)
    {
        return FluidRegistry.getFluid("molten" + metal);
    }
}
