package net.ros.common.grid.node;

import lombok.Data;

@Data
public class PipeType
{
    private PipeNature nature;
    private PipeSize   size;
    private String     material;

    public enum PipeNature
    {
        FLUID,
        STEAM
    }

    public enum PipeSize
    {
        SMALL,
        MEDIUM,
        LARGE,
        HUGE,
        EXTRA_HUGE
    }
}
