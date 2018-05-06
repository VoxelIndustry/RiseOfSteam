package net.ros.common.machine.component;

import lombok.Getter;
import lombok.Setter;
import net.ros.common.machine.IMachineComponent;
import net.ros.common.machine.InputPoint;
import net.ros.common.machine.MachineDescriptor;
import net.ros.common.machine.OutputPoint;

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