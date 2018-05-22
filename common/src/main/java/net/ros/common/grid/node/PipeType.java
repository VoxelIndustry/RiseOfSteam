package net.ros.common.grid.node;

import lombok.Data;
import net.ros.common.recipe.Metal;

@Data
public class PipeType
{
    private PipeNature nature;
    private PipeSize   size;
    private Metal      metal;

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
