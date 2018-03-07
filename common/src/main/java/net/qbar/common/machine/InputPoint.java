package net.qbar.common.machine;

import lombok.Data;
import net.qbar.common.multiblock.MultiblockSide;

@Data
public class InputPoint
{
    private MultiblockSide side;
    private Integer[]      slots;
}