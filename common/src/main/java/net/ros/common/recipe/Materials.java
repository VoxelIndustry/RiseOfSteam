package net.ros.common.recipe;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class Materials
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
        metals.addMetal(IRON).shapes(MaterialShape.PLATE, MaterialShape.GEAR);
        metals.addMetal(GOLD).shapes(MaterialShape.PLATE);
        metals.addMetal(COPPER).shapes(MaterialShape.NUGGET, MaterialShape.PLATE, MaterialShape.GEAR, MaterialShape.INGOT, MaterialShape.BLOCK);
        metals.addMetal(BRONZE).shapes(MaterialShape.NUGGET, MaterialShape.PLATE, MaterialShape.GEAR, MaterialShape.INGOT, MaterialShape.BLOCK);
        metals.addMetal(BRASS).shapes(MaterialShape.NUGGET, MaterialShape.PLATE, MaterialShape.GEAR, MaterialShape.INGOT, MaterialShape.BLOCK);
        metals.addMetal(TIN).shapes(MaterialShape.NUGGET, MaterialShape.PLATE, MaterialShape.INGOT, MaterialShape.BLOCK);
        metals.addMetal(ZINC).shapes(MaterialShape.NUGGET, MaterialShape.PLATE, MaterialShape.INGOT, MaterialShape.BLOCK);
        metals.addMetal(NICKEL).shapes(MaterialShape.NUGGET, MaterialShape.PLATE, MaterialShape.INGOT, MaterialShape.BLOCK);
        metals.addMetal(LEAD).shapes(MaterialShape.NUGGET, MaterialShape.PLATE, MaterialShape.INGOT, MaterialShape.BLOCK);
        metals.addMetal(STEEL).shapes(MaterialShape.NUGGET, MaterialShape.PLATE, MaterialShape.GEAR, MaterialShape.INGOT, MaterialShape.BLOCK);
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
        if (!cachedMetalVariants.contains(metal, MaterialShape.PLATE))
        {
            ItemStack stack = OreDictionary.getOres("plate" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, MaterialShape.PLATE, stack);
        }
        return cachedMetalVariants.get(metal, MaterialShape.PLATE);
    }

    public static ItemStack getGearFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, MaterialShape.GEAR))
        {
            ItemStack stack = OreDictionary.getOres("gear" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, MaterialShape.GEAR, stack);
        }
        return cachedMetalVariants.get(metal, MaterialShape.GEAR);
    }

    public static ItemStack getIngotFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, MaterialShape.INGOT))
        {
            ItemStack stack = OreDictionary.getOres("ingot" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, MaterialShape.INGOT, stack);
        }
        return cachedMetalVariants.get(metal, MaterialShape.INGOT);
    }

    public static ItemStack getBlockFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, MaterialShape.BLOCK))
        {
            ItemStack stack = OreDictionary.getOres("block" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, MaterialShape.BLOCK, stack);
        }
        return cachedMetalVariants.get(metal, MaterialShape.BLOCK);
    }

    public static ItemStack getNuggetFromMetal(String metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, MaterialShape.NUGGET))
        {
            ItemStack stack = OreDictionary.getOres("nugget" + StringUtils.capitalize(metal)).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, MaterialShape.NUGGET, stack);
        }
        return cachedMetalVariants.get(metal, MaterialShape.NUGGET);
    }

    public static Fluid getFluidFromMetal(String metal)
    {
        return FluidRegistry.getFluid("molten" + metal);
    }
}
