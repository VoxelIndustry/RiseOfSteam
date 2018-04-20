package net.qbar.common.machine.component;

import lombok.Getter;
import lombok.Setter;
import net.qbar.common.machine.FluidIOPoint;
import net.qbar.common.machine.IMachineComponent;
import net.qbar.common.machine.MachineDescriptor;
import net.qbar.common.multiblock.MultiblockSide;

@Getter
@Setter
public class IOComponent implements IMachineComponent
{
    private MachineDescriptor descriptor;

    private MultiblockSide[] steamIO;
    private FluidIOPoint[] fluidIO;
}
