package net.ros.common.steam;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.init.ROSFluids;

public class SteamTank implements ISteamTank
{
    @Getter
    private FluidStack fluidStack;
    @Getter
    @Setter
    private int        steam;

    @Getter
    @Setter
    private int   capacity;
    @Getter
    private float maxPressure;
    @Getter
    private float safePressure;


    public SteamTank(int steamAmount, int capacity, float maxPressure, float safePressure)
    {
        this.steam = steamAmount;
        this.capacity = capacity;
        this.maxPressure = maxPressure;
        this.safePressure = safePressure;
    }

    public SteamTank(int steamAmount, int capacity, float maxPressure)
    {
        this(steamAmount, capacity, maxPressure, maxPressure);
    }

    public SteamTank(NBTTagCompound tag)
    {
        this(tag.getInteger("steam"), tag.getInteger("capacity"), tag.getInteger("maxPressure"));
    }

    @Override
    public int drainSteam(final int amount, final boolean doDrain)
    {
        return this.drainInternal(amount, doDrain);
    }

    public int drainInternal(final int amount, final boolean doDrain)
    {
        int drained = Math.min(amount, this.getSteam());
        if (doDrain)
            this.setSteam(this.getSteam() - drained);
        return drained;
    }

    @Override
    public int fillSteam(final int amount, final boolean doFill)
    {
        return this.fillInternal(amount, doFill);
    }

    public int fillInternal(final int amount, final boolean doFill)
    {
        int filled = amount;

        filled = (int) Math.min(filled, this.getCapacity() * this.getMaxPressure() - this.getSteam());
        if (doFill)
            this.setSteam(this.getSteam() + filled);
        return filled;
    }

    public void readFromNBT(final NBTTagCompound nbt)
    {
        this.setSteam(nbt.getInteger("steam"));
        this.capacity = nbt.getInteger("capacity");
        this.maxPressure = nbt.getFloat("maxPressure");
    }

    public NBTTagCompound writeToNBT(final NBTTagCompound nbt)
    {
        nbt.setInteger("steam", this.getSteam());
        nbt.setInteger("capacity", this.capacity);
        nbt.setFloat("maxPressure", this.maxPressure);
        return nbt;
    }

    @Override
    public float getPressure()
    {
        return (float) this.getSteam() / this.getCapacity();
    }

    @Override
    public FluidStack toFluidStack()
    {
        if (this.fluidStack == null)
            this.fluidStack = new FluidStack(ROSFluids.fluidSteam, 0);
        this.fluidStack.amount = this.getSteam();
        return this.fluidStack;
    }

    @Override
    public boolean canFill()
    {
        return true;
    }

    @Override
    public boolean canDrain()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "SteamTank{" +
                "steam=" + steam +
                ", capacity=" + capacity +
                ", maxPressure=" + maxPressure +
                ", safePressure=" + safePressure +
                '}';
    }
}