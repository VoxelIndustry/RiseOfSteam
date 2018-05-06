package net.ros.common.recipe;

public enum MaterialShape
{
    NUGGET, INGOT, GEAR, PLATE, BLOCK;

    public String toString()
    {
        return this.name().toLowerCase();
    }
}
