package net.ros.common.machine;

import lombok.Data;
import net.ros.common.multiblock.MultiblockSide;

@Data
public class OutputPoint
{
    private MultiblockSide side;
    private int[]          slots;
    private boolean        roundRobin;
    private String         inventory;
    // Used for belt placement preview
    private String         connText;
}
