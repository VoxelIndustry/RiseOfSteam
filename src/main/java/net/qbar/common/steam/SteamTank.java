package net.qbar.common.steam;

import net.minecraft.nbt.NBTTagCompound;

public class SteamTank implements ISteamTank
{
    private SteamStack steam;

    private int        capacity;
    private int        maxPressure;

    public SteamTank(final SteamStack content, final int capacity, final int maxPressure)
    {
        this.steam = content;
        this.capacity = capacity;
        this.maxPressure = maxPressure;
    }

    public SteamTank(final int amount, final int pressure, final int capacity, final int maxPressure)
    {
        this(new SteamStack(amount, pressure), capacity, maxPressure);
    }

    @Override
    public SteamStack drainSteam(final int amount, final boolean simulated)
    {
        // TODO : default implementation
        return null;
    }

    @Override
    public SteamStack fillSteam(final SteamStack steam, final boolean simulated)
    {
        // TODO : default implementation
        return null;
    }

    public void readFromNBT(final NBTTagCompound nbt)
    {
        if (!nbt.hasKey("Empty"))
            this.setSteam(SteamStack.readFromNBT(nbt));
        this.capacity = nbt.getInteger("capacity");
        this.maxPressure = nbt.getInteger("maxPressure");
    }

    public void writeToNBT(final NBTTagCompound nbt)
    {
        if (this.getSteam() != null)
            this.getSteam().writeToNBT(nbt);
        else
            nbt.setString("Empty", "");
        nbt.setInteger("capacity", this.capacity);
        nbt.setInteger("maxPressure", this.maxPressure);
    }

    @Override
    public SteamStack getSteam()
    {
        return this.steam;
    }

    public void setSteam(final SteamStack stack)
    {
        this.steam = stack;
    }

    @Override
    public int getAmount()
    {
        return this.getSteam().getAmount();
    }

    @Override
    public int getCapacity()
    {
        return this.capacity;
    }

    @Override
    public int getPressure()
    {
        return this.getSteam().getPressure();
    }

    @Override
    public int getMaxPressure()
    {
        return this.maxPressure;
    }
}