package net.qbar.common.machine;

import lombok.Data;
import net.qbar.common.multiblock.MultiblockSide;

@Data
public class FluidIOPoint
{
    private MultiblockSide side;
    private boolean input;
    private boolean output;

    private String tankName;
}
