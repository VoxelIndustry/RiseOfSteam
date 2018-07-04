package net.ros.common.ore;

import net.minecraft.util.IStringSerializable;

public enum MineralDensity implements IStringSerializable
{
    POOR, NORMAL, RICH;

    public static MineralDensity[] VALUES = new MineralDensity[]{POOR, NORMAL, RICH};

    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }

    public static MineralDensity fromValue(float density)
    {
        if (density >= 0.75f)
            return RICH;
        if (density >= 0.5f)
            return NORMAL;
        return POOR;
    }

    @Override
    public String getName()
    {
        return this.name().toLowerCase();
    }

    public int getFluidAmount()
    {
        switch (this)
        {
            case POOR:
                return 125;
            case NORMAL:
                return 250;
            case RICH:
                return 500;
        }
        return 0;
    }
}
