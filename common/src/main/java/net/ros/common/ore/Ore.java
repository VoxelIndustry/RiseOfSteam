package net.ros.common.ore;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.minecraftforge.fluids.Fluid;

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
    private Slag slag;

    public Fluid toSludge()
    {
        return Ores.toSludge(this);
    }
}
