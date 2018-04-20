package net.qbar.common.machine.component;

import lombok.Getter;
import lombok.Setter;
import net.qbar.common.machine.IMachineComponent;
import net.qbar.common.machine.InputPoint;
import net.qbar.common.machine.MachineDescriptor;
import net.qbar.common.machine.OutputPoint;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AutomationComponent implements IMachineComponent
{
    private List<OutputPoint> outputs;
    private List<InputPoint>  inputs;

    private MachineDescriptor descriptor;

    public AutomationComponent()
    {
        this.outputs = new ArrayList<>();
        this.inputs = new ArrayList<>();
    }
}