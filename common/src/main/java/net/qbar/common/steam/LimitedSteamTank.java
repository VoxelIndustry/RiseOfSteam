package net.qbar.common.steam;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;

public class LimitedSteamTank extends SteamTank
{
    @Getter
    @Setter
    private int throttle;

    public LimitedSteamTank(int steamAmount, int capacity, float maxPressure, int throttle)
    {
        super(steamAmount, capacity, maxPressure);

        this.throttle = throttle;
    }

    public LimitedSteamTank(NBTTagCompound tag)
    {
        super(tag);
    }

    @Override
    public int drainSteam(int amount, boolean doDrain)
    {
        return super.drainSteam(Math.min(amount, this.throttle), doDrain);
    }

    @Override
    public int fillSteam(int amount, boolean doFill)
    {
        return super.fillSteam(Math.min(amount, this.throttle), doFill);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("throttle", this.throttle);

        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        this.throttle = nbt.getInteger("throttle");
    }
}
