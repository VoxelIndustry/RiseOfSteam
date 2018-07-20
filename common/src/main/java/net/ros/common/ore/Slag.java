package net.ros.common.ore;

public enum Slag
{
    FERROUS, COPPER, SPARKLING, SHINY, TIN;

    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
}
