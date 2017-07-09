package net.qbar.common.world;

import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.Range;

import java.util.HashMap;

@Getter
public class OreVeinDescriptor
{
    private HashMap<IBlockState, Float> contents;
    private BiomeMatcher.BiomePredicate biomeMatcher;

    private float                       heapDensity;
    private int                         heapQty;
    private int                         heapSize;
    private Range<Integer>              heightRange;
    private EVeinHeapForm               heapForm;
    private EVeinForm                   veinForm;

    private float                       rarity;

    private String                      name;

    public OreVeinDescriptor(String name)
    {
        this.contents = new HashMap<>();
        this.name = name;
    }

    public OreVeinDescriptor content(IBlockState state, float proportion)
    {
        this.contents.put(state, (float) (proportion + this.contents.values().stream().mapToDouble(i -> i).sum()));
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
        this.heightRange = Range.between(minY, maxY);
        return this;
    }

    @Override
    public String toString()
    {
        return "OreVeinDescriptor{" + "name='" + name + '\'' + '}';
    }
}
