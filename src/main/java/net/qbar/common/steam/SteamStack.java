package net.qbar.common.steam;

import net.minecraft.nbt.NBTTagCompound;

public class SteamStack
{
    private int amount;

    public SteamStack(final int amount)
    {
        this.amount = amount;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public void setAmount(final int amount)
    {
        this.amount = amount;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.amount;
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
        return true;
    }

    @Override
    public String toString()
    {
        return "SteamStack [amount=" + this.amount + "]";
    }

    public void writeToNBT(final NBTTagCompound nbt)
    {
        nbt.setInteger("amount", this.getAmount());
    }

    public static SteamStack readFromNBT(final NBTTagCompound nbt)
    {
        SteamStack stack = null;
        if (nbt.hasKey("amount"))
            stack = new SteamStack(nbt.getInteger("amount"));
        return stack;
    }
}
