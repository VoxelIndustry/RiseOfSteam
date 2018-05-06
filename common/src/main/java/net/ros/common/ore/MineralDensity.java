package net.ros.common.ore;

public enum MineralDensity
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
}
