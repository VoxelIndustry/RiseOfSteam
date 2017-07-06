package net.qbar.common.world;

import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.Range;

import java.util.HashMap;

public class OreVeinDescriptor
{
    private HashMap<IBlockState, Float> contents;
    private BiomeMatcher.BiomePredicate biomeMatcher;

    private float          heapDensity;
    private int            heapQty;
    private int            heapSize;
    private Range<Integer> yRange;
    private EVeinHeapForm  heapForm;
    private EVeinForm      veinForm;

    private float rarity;

    public OreVeinDescriptor()
    {
        this.contents = new HashMap<>();
    }

    public OreVeinDescriptor content(IBlockState state, float proportion)
    {
        this.contents.put(state, proportion);
        return this;
    }

    public OreVeinDescriptor biomes(BiomeMatcher.BiomePredicate biomeMatcher)
    {
        this.biomeMatcher = biomeMatcher;
        return this;
    }

    public OreVeinDescriptor heapDensity(float density)
    {
        this.heapDensity = density;
        return this;
    }

    public OreVeinDescriptor heapQty(int heapQty)
    {
        this.heapQty = heapQty;
        return this;
    }

    public OreVeinDescriptor heapSize(int heapSize)
    {
        this.heapSize = heapSize;
        return this;
    }

    public OreVeinDescriptor heapForm(EVeinHeapForm form)
    {
        this.heapForm = form;
        return this;
    }

    public OreVeinDescriptor veinForm(EVeinForm form)
    {
        this.veinForm = form;
        return this;
    }

    public OreVeinDescriptor rarity(float rarity)
    {
        this.rarity = rarity;
        return this;
    }

    public OreVeinDescriptor heightRange(int minY, int maxY)
    {
        this.yRange = Range.between(minY, maxY);
        return this;
    }

    public HashMap<IBlockState, Float> getContents()
    {
        return contents;
    }

    public BiomeMatcher.BiomePredicate getBiomeMatcher()
    {
        return biomeMatcher;
    }

    public float getHeapDensity()
    {
        return heapDensity;
    }

    public int getHeapQty()
    {
        return heapQty;
    }

    public int getHeapSize()
    {
        return heapSize;
    }

    public EVeinHeapForm getHeapForm()
    {
        return heapForm;
    }

    public EVeinForm getVeinForm()
    {
        return veinForm;
    }

    public float getRarity()
    {
        return rarity;
    }

    public Range<Integer> getHeightRange()
    {
        return yRange;
    }
}
