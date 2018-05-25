package net.ros.common.recipe;

public enum MaterialShape
{
    NUGGET, INGOT, GEAR, PLATE, BLOCK, BLOCK_PLATE("blockPlate"), SCAFFOLD;

    private String oredict;

    MaterialShape()
    {
        this(null);
    }

    MaterialShape(String oredict)
    {
        this.oredict = oredict;
    }

    public String toString()
    {
        return this.name().toLowerCase();
    }

    public String getOreDict()
    {
        if (oredict == null)
            return this.toString();
        return oredict;
    }
}
