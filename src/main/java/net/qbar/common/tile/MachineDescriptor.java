package net.qbar.common.tile;

public class MachineDescriptor
{
    private final String name;

    private final int   steamCapacity;
    private final float workingPressure, maxPressureCapacity;
    private final int     steamConsumption;
    private final boolean allowOvercharge;

    /**
     * @param name
     * @param steamCapacity
     * @param workingPressure
     * @param maxPressureCapacity
     * @param allowOvercharge
     */
    public MachineDescriptor(final String name, final int steamCapacity, final int steamConsumption,
                             final float workingPressure, final float maxPressureCapacity, final boolean allowOvercharge)
    {
        this.name = name;
        this.steamCapacity = steamCapacity;
        this.steamConsumption = steamConsumption;
        this.workingPressure = workingPressure;
        this.maxPressureCapacity = maxPressureCapacity;
        this.allowOvercharge = allowOvercharge;
    }

    public String getName()
    {
        return this.name;
    }

    public int getSteamCapacity()
    {
        return this.steamCapacity;
    }

    public int getSteamConsumption()
    {
        return this.steamConsumption;
    }

    public float getWorkingPressure()
    {
        return this.workingPressure;
    }

    public float getMaxPressureCapacity()
    {
        return this.maxPressureCapacity;
    }

    public boolean allowOvercharge()
    {
        return this.allowOvercharge;
    }
}