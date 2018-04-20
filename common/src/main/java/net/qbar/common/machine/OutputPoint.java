package net.qbar.common.machine;

import lombok.Data;
import net.qbar.common.multiblock.MultiblockSide;

@Data
public class OutputPoint
{
    private MultiblockSide side;
    private int[]      slots;
    private boolean        roundRobin;
    private String         inventory;
}
