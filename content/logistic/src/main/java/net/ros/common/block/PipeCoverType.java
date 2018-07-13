package net.ros.common.block;

import lombok.Getter;

public enum PipeCoverType
{
    STEAM_GAUGE("gauge"),
    FLUID_GAUGE("gauge"),
    VALVE("valve"),
    PRESSURE_VALVE("pressurevalve"),
    FLUID_PUMP("pump"),
    STEAM_VENT("vent");

    @Getter
    private String prefix;

    PipeCoverType(String prefix)
    {
        this.prefix = prefix;
    }
}
