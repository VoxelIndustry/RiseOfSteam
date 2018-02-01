package net.qbar.common.recipe;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public class MetalList
{
    private List<String>                 metalNames;
    private List<EnumSet<MaterialShape>> metalShapes;

    public MetalList()
    {
        this.metalNames = new ArrayList<>();
        this.metalShapes = new ArrayList<>();
    }

    public MetalEntry addMetal(String name)
    {
        this.metalNames.add(name);
        return new MetalEntry();
    }

    public Stream<String> stream()
    {
        return this.metalNames.stream();
    }

    public int indexOf(String metal)
    {
        return this.metalNames.indexOf(metal);
    }

    public boolean contains(String metal)
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

    public String get(int index)
    {
        return metalNames.get(index);
    }

    public boolean containsShape(String metal, MaterialShape shape)
    {
        return this.metalShapes.get(metalNames.indexOf(metal)).contains(shape);
    }

    public EnumSet<MaterialShape> getShapes(String metal)
    {
        return this.metalShapes.get(metalNames.indexOf(metal));
    }

    public List<String> getAllMetalForShape(MaterialShape shape)
    {
        List<String> metals = new ArrayList<>();
        for(int i = 0; i < this.metalShapes.size();i++)
        {
            if(metalShapes.get(i).contains(shape))
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
