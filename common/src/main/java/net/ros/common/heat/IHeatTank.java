package net.ros.common.heat;

import net.minecraft.nbt.NBTTagCompound;

public interface IHeatTank extends IHeatHandler
{
    void readFromNBT(NBTTagCompound nbt);

    NBTTagCompound writeToNBT(NBTTagCompound nbt);

    void setHeat(int heat);
}
