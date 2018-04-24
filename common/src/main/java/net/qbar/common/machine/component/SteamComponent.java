package net.qbar.common.machine.component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.qbar.common.machine.IMachineComponent;
import net.qbar.common.machine.MachineDescriptor;

@Getter
@Setter
@NoArgsConstructor
public class SteamComponent implements IMachineComponent
{
    private int               steamCapacity;
    private float             workingPressure;
    private float             maxPressureCapacity;
    private float             safePressureCapacity;
    private int               steamConsumption;
    private boolean           allowOvercharge;
    private MachineDescriptor descriptor;
}
