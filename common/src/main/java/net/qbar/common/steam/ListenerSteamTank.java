package net.qbar.common.steam;

import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class ListenerSteamTank implements ISteamTank
{
    private final ISteamTank delegate;

    @Setter
    private Runnable onSteamChange;

    private int lastSteam = -1;

    public ListenerSteamTank(ISteamTank delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public FluidStack toFluidStack()
    {
        return delegate.toFluidStack();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        delegate.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        return delegate.writeToNBT(nbt);
    }

    @Override
    public void setSteam(int steam)
    {
        delegate.setSteam(steam);

        if (onSteamChange != null && lastSteam != this.getSteam())
        {
            onSteamChange.run();
            lastSteam = getSteam();
        }
    }

    @Override
    public int drainSteam(int amount, boolean doDrain)
    {
        int drained = delegate.drainSteam(amount, doDrain);

        if (onSteamChange != null && lastSteam != this.getSteam())
        {
            onSteamChange.run();
            lastSteam = getSteam();
        }
        return drained;
    }

    @Override
    public int fillSteam(int steam, boolean doFill)
    {
        int filled = delegate.fillSteam(steam, doFill);

        if (onSteamChange != null && lastSteam != this.getSteam())
        {
            onSteamChange.run();
            lastSteam = getSteam();
        }
        return filled;
    }

    @Override
    public boolean canFill()
    {
        return delegate.canFill();
    }

    @Override
    public boolean canDrain()
    {
        return delegate.canDrain();
    }

    @Override
    public float getPressure()
    {
        return delegate.getPressure();
    }

    @Override
    public float getMaxPressure()
    {
        return delegate.getMaxPressure();
    }

    @Override
    public int getSteam()
    {
        return delegate.getSteam();
    }

    @Override
    public int getCapacity()
    {
        return delegate.getCapacity();
    }

    @Override
    public float getSafePressure()
    {
        return delegate.getSafePressure();
    }

    @Override
    public int getActualCapacity()
    {
        return delegate.getActualCapacity();
    }

    @Override
    public int getFreeSpace()
    {
        return delegate.getFreeSpace();
    }

    @Override
    public int getSteamDifference(float fromPressure)
    {
        return delegate.getSteamDifference(fromPressure);
    }
}
