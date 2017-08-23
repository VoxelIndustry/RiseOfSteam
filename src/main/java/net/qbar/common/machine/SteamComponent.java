package net.qbar.common.machine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SteamComponent implements IMachineComponent
{
    private int               steamCapacity;
    private float             workingPressure;
    private float             maxPressureCapacity;
    private int               steamConsumption;
    private boolean           allowOvercharge;
    private MachineDescriptor descriptor;
}
