package net.qbar.common.tile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MachineDescriptor
{
    private final String name;

    private final int   steamCapacity;
    private final float workingPressure;
    private final float maxPressureCapacity;
    private final int     steamConsumption;
    private final boolean allowOvercharge;
}