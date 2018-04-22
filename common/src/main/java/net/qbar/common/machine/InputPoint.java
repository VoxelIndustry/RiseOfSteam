package net.qbar.common.machine;

import lombok.Data;
import net.qbar.common.multiblock.MultiblockSide;

@Data
public class InputPoint
{
    private MultiblockSide side;
    private int[]          slots;
    private String         inventory;
    // Used for belt placement preview
    private String         connText;
}