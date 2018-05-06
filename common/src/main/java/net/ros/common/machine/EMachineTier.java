package net.ros.common.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EMachineTier
{
    TIER0("wood"),
    TIER1("bronze"),
    TIER2("steel");

    private String tierName;
}
