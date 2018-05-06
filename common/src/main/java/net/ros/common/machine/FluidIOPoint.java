package net.ros.common.machine;

import lombok.Data;
import net.ros.common.multiblock.MultiblockSide;

@Data
public class FluidIOPoint
{
    private MultiblockSide side;
    private boolean        input;
    private boolean        output;

    private String tankName;
}
