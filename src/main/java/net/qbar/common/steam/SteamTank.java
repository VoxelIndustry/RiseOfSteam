package net.qbar.common.steam;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.qbar.common.init.QBarFluids;

public class SteamTank implements ISteamTank
{
    private final FluidStack fluidStack;
    private SteamStack       steam;

    private int              capacity;
    private float            maxPressure;

    public SteamTank(final SteamStack content, final int capacity, final float maxPressure)
    {
        this.steam = content;
        this.capacity = capacity;
        this.maxPressure = maxPressure;

        this.fluidStack = new FluidStack(QBarFluids.fluidSteam, 0);
    }

    public SteamTank(final int amount, final int capacity, final int maxPressure)
    {
        this(new SteamStack(amount), capacity, maxPressure);
    }

    @Override
    public SteamStack drainSteam(final int amount, final boolean simulated)
    {
        int drained = amount;

        drained = Math.min(amount, this.steam.getAmount());
        if (simulated)
        {
            this.steam.setAmount(this.steam.getAmount() - drained);
        }
        return new SteamStack(drained);
    }

    @Override
    public int fillSteam(@Nonnull final SteamStack steam, final boolean simulated)
    {
        return this.fillSteam(steam.getAmount(), simulated);
    }

    public int fillSteam(final int amount, final boolean simulated)
    {
        int filled = amount;

        filled = (int) Math.min(filled, this.getCapacity() * this.getMaxPressure() - this.getAmount());
        if (simulated)
        {
            if (this.steam != null)
                this.steam.setAmount(this.steam.getAmount() + filled);
            else
                this.steam = new SteamStack(filled);
        }
        return filled;
    }

    public void readFromNBT(final NBTTagCompound nbt)
    {
        if (!nbt.hasKey("Empty"))
            this.setSteam(SteamStack.readFromNBT(nbt));
        this.capacity = nbt.getInteger("capacity");
        this.maxPressure = nbt.getFloat("maxPressure");
    }

    public void writeToNBT(final NBTTagCompound nbt)
    {
        if (this.getSteam() != null)
            this.getSteam().writeToNBT(nbt);
        else
            nbt.setString("Empty", "");
        nbt.setInteger("capacity", this.capacity);
        nbt.setFloat("maxPressure", this.maxPressure);
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

    public void setAmount(final int amount)
    {
        this.getSteam().setAmount(amount);
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
    public float getPressure()
    {
        return (float) this.getAmount() / this.getCapacity();
    }

    @Override
    public float getMaxPressure()
    {
        return this.maxPressure;
    }

    public FluidStack toFluidStack()
    {
        this.fluidStack.amount = this.getAmount();
        return this.fluidStack;
    }
}