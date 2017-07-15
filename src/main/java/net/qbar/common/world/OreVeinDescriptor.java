package net.qbar.common.world;

import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class OreVeinDescriptor
{
    private List<Pair<IBlockState, Float>> contents;
    private List<VeinMarker>               markers;
    private BiomeMatcher.BiomePredicate    biomeMatcher;

    private float                          heapDensity;
    private int                            heapQty;
    private int                            heapSize;
    private Range<Integer>                 heightRange;
    private EVeinHeapForm                  heapForm;
    private EVeinForm                      veinForm;

    private float                          rarity;

    private String                         name;

    public OreVeinDescriptor(String name)
    {
        this.contents = new ArrayList<>();
        this.markers = new ArrayList<>();
        this.name = name;
    }

    public OreVeinDescriptor content(IBlockState state, float proportion)
    {
        this.contents.add(Pair.of(state, proportion + this.contents.stream().max(Comparator.comparing(Pair::getRight))
                .orElse(Pair.of(null, 0F)).getRight()));
        return this;
    }

    public OreVeinDescriptor marker(VeinMarker marker)
    {
        this.markers.add(marker);
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
