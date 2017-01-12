package net.qbar.common.steam;

import net.minecraft.nbt.NBTTagCompound;

public class SteamStack
{
    private int amount;
    private int pressure;

    public SteamStack(final int amount, final int pressure)
    {
        this.amount = amount;
        this.pressure = pressure;
    }

    public SteamStack(final int amount)
    {
        this(amount, SteamUtil.AMBIANT_PRESSURE);
    }

    public int getAmount()
    {
        return this.amount;
    }

    public void setAmount(final int amount)
    {
        this.amount = amount;
    }

    public int getPressure()
    {
        return this.pressure;
    }

    public void setPressure(final int pressure)
    {
        this.pressure = pressure;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.amount;
        result = prime * result + this.pressure;
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        final SteamStack other = (SteamStack) obj;
        if (this.amount != other.amount)
            return false;
        if (this.pressure != other.pressure)
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "SteamStack [amount=" + this.amount + ", pressure=" + this.pressure + "]";
    }

    public void writeToNBT(final NBTTagCompound nbt)
    {
        nbt.setInteger("amount", this.getAmount());
        nbt.setInteger("pressure", this.getPressure());
    }

    public static SteamStack readFromNBT(final NBTTagCompound nbt)
    {
        SteamStack stack = null;
        if (nbt.hasKey("amount") && nbt.hasKey("pressure"))
            stack = new SteamStack(nbt.getInteger("amount"), nbt.getInteger("pressure"));
        return stack;
    }
}
