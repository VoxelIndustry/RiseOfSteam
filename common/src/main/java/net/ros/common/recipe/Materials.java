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

import static net.ros.common.recipe.MaterialShape.*;

public class Materials
{
    public static final Metal IRON   = Metal.builder().name("iron").meltingPoint(1204).build();
    public static final Metal GOLD   = Metal.builder().name("gold").meltingPoint(1064).build();
    public static final Metal COPPER = Metal.builder().name("copper").meltingPoint(1085).build();
    public static final Metal BRONZE = Metal.builder().name("bronze").meltingPoint(950).isAlloy(true).build();
    public static final Metal BRASS  = Metal.builder().name("brass").meltingPoint(927).isAlloy(true).build();
    public static final Metal TIN    = Metal.builder().name("tin").meltingPoint(232).build();
    public static final Metal ZINC   = Metal.builder().name("zinc").meltingPoint(419).build();
    public static final Metal NICKEL = Metal.builder().name("nickel").meltingPoint(1455).build();
    public static final Metal LEAD   = Metal.builder().name("lead").meltingPoint(327).build();
    public static final Metal STEEL  = Metal.builder().name("steel").meltingPoint(1371).build();

    public static MetalList metals = new MetalList();

    private static final Table<Metal, MaterialShape, ItemStack> cachedMetalVariants = HashBasedTable.create();

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

    public static Optional<Metal> getMetalFromFluid(FluidStack moltenMetal)
    {
        return metals.stream()
                .filter(metal -> metal.equals(moltenMetal.getFluid().getName().replace("molten", "")))
                .findAny();
    }

    public static ItemStack getPlateFromMetal(Metal metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, PLATE))
        {
            ItemStack stack = OreDictionary.getOres("plate" + StringUtils.capitalize(metal.getName())).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, PLATE, stack);
        }
        return cachedMetalVariants.get(metal, PLATE);
    }

    public static ItemStack getGearFromMetal(Metal metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, GEAR))
        {
            ItemStack stack = OreDictionary.getOres("gear" + StringUtils.capitalize(metal.getName())).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, GEAR, stack);
        }
        return cachedMetalVariants.get(metal, GEAR);
    }

    public static ItemStack getIngotFromMetal(Metal metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, INGOT))
        {
            ItemStack stack = OreDictionary.getOres("ingot" + StringUtils.capitalize(metal.getName())).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, INGOT, stack);
        }
        return cachedMetalVariants.get(metal, INGOT);
    }

    public static ItemStack getBlockFromMetal(Metal metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, BLOCK))
        {
            ItemStack stack = OreDictionary.getOres("block" + StringUtils.capitalize(metal.getName())).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, BLOCK, stack);
        }
        return cachedMetalVariants.get(metal, BLOCK);
    }

    public static ItemStack getNuggetFromMetal(Metal metal)
    {
        if (!metals.contains(metal))
            return ItemStack.EMPTY;
        if (!cachedMetalVariants.contains(metal, NUGGET))
        {
            ItemStack stack = OreDictionary.getOres("nugget" + StringUtils.capitalize(metal.getName())).get(0).copy();
            stack.setCount(1);
            cachedMetalVariants.put(metal, NUGGET, stack);
        }
        return cachedMetalVariants.get(metal, NUGGET);
    }

    public static Fluid getFluidFromMetal(Metal metal)
    {
        return FluidRegistry.getFluid("molten" + metal.getName());
    }

    public static FluidStack getFluidStackFromMetal(Metal metal, int quantity)
    {
        return FluidRegistry.getFluidStack("molten" + metal.getName(), quantity);
    }
}
