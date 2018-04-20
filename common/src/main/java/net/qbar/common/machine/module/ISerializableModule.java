package net.qbar.common.machine.module;

import net.minecraft.nbt.NBTTagCompound;

public interface ISerializableModule
{
    void fromNBT(NBTTagCompound tag);

    NBTTagCompound toNBT(NBTTagCompound tag);
}
