package net.ros.common.recipe;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MetalList
{
    private List<Metal>                  metalNames;
    private List<EnumSet<MaterialShape>> metalShapes;

    public MetalList()
    {
        this.metalNames = new ArrayList<>();
        this.metalShapes = new ArrayList<>();
    }

    public MetalEntry addMetal(Metal name)
    {
        this.metalNames.add(name);
        return new MetalEntry();
    }

    public Optional<Metal> byName(String metalName)
    {
        return this.metalNames.stream().filter(metal -> metal.getName().equals(metalName)).findFirst();
    }

    public Stream<Metal> stream()
    {
        return this.metalNames.stream();
    }

    public int indexOf(Metal metal)
    {
        return this.metalNames.indexOf(metal);
    }

    public boolean contains(Metal metal)
    {
        return this.metalNames.contains(metal);
    }

    public <T> T[] toArray(T[] a)
    {
        return metalNames.toArray(a);
    }

    public int size()
    {
        return metalNames.size();
    }

    public Metal get(int index)
    {
        return metalNames.get(index);
    }

    public boolean containsShape(Metal metal, MaterialShape shape)
    {
        return this.metalShapes.get(metalNames.indexOf(metal)).contains(shape);
    }

    public EnumSet<MaterialShape> getShapes(Metal metal)
    {
        return this.metalShapes.get(metalNames.indexOf(metal));
    }

    public List<Metal> getAllMetalForShape(MaterialShape shape)
    {
        List<Metal> metals = new ArrayList<>();
        for (int i = 0; i < this.metalShapes.size(); i++)
        {
            if (metalShapes.get(i).contains(shape))
                metals.add(metalNames.get(i));
        }
        return metals;
    }

    public class MetalEntry
    {
        public void shapes(MaterialShape first, MaterialShape... rest)
        {
            metalShapes.add(EnumSet.of(first, rest));
        }
    }
}
