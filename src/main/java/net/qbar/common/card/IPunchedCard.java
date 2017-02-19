package net.qbar.common.card;

import net.minecraft.nbt.NBTTagCompound;

public interface IPunchedCard
{
    void readFromNBT(NBTTagCompound compound);

    NBTTagCompound writeToNBT(NBTTagCompound compound);

}
