package net.ros.common.grid.node;

public enum PipeNature
{
    FLUID,
    STEAM,
    HEAT;

    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
}
