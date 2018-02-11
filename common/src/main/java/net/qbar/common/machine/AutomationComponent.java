package net.qbar.common.machine;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AutomationComponent implements IMachineComponent
{
    private List<OutputPoint> outputs;

    private MachineDescriptor descriptor;

    public AutomationComponent()
    {
        this.outputs = new ArrayList<>();
    }
}