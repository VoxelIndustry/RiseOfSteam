package net.ros.common.ore;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

@Getter
@Builder
public class Ore
{
    private String              name;
    @Singular
    private Map<Mineral, Float> minerals;

    private int toolLevel;
    private float hardness;
    private float resistance;

    public SludgeData toSludge()
    {
        return new SludgeData().addOres(minerals);
    }
}
