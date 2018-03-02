package net.qbar.common.machine.component;

import lombok.Getter;
import lombok.Setter;
import net.qbar.common.machine.IMachineComponent;
import net.qbar.common.machine.MachineDescriptor;

@Getter
@Setter
public class IOComponent implements IMachineComponent
{
    private MachineDescriptor descriptor;
}
