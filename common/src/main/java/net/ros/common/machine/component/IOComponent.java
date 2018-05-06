package net.ros.common.machine.component;

import lombok.Getter;
import lombok.Setter;
import net.ros.common.machine.FluidIOPoint;
import net.ros.common.machine.IMachineComponent;
import net.ros.common.machine.MachineDescriptor;
import net.ros.common.multiblock.MultiblockSide;

@Getter
@Setter
public class IOComponent implements IMachineComponent
{
    private MachineDescriptor descriptor;

    private MultiblockSide[] steamIO;
    private FluidIOPoint[]   fluidIO;
}
