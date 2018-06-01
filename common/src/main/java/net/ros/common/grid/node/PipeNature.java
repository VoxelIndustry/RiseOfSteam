package net.ros.common.grid.node;

public enum PipeNature
{
    FLUID,
    STEAM;

    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
}
