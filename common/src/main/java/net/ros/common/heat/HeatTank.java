package net.ros.common.heat;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;

@Getter
public class HeatTank implements IHeatTank
{
    @Setter
    private int heat;
    @Setter
    private int capacity;

    public HeatTank(int capacity)
    {
        this.heat = 0;
        this.capacity = capacity;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.setHeat(nbt.getInteger("heat"));
        this.capacity = nbt.getInteger("capacity");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("heat", this.getHeat());
        nbt.setInteger("capacity", this.getCapacity());

        return nbt;
    }

    @Override
    public int drainHeat(int amount, boolean doDrain)
    {
        int drained = Math.min(amount, this.getHeat());
        if (doDrain)
            this.setHeat(this.getHeat() - drained);
        return drained;
    }

    @Override
    public int fillHeat(int amount, boolean doFill)
    {
        int filled = Math.min(amount, this.getFreeHeat());
        if (doFill)
            this.setHeat(this.getHeat() + filled);
        return filled;
    }

    @Override
    public String toString()
    {
        return "HeatTank{" +
                "heat=" + heat +
                ", capacity=" + capacity +
                '}';
    }
}
