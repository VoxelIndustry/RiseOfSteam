package net.ros.common.machine;

import lombok.Data;
import net.ros.common.multiblock.MultiblockSide;

@Data
public class InputPoint
{
    private MultiblockSide side;
    private int[]          slots;
    private String         inventory;
    // Used for belt placement preview
    private String         connText;
}