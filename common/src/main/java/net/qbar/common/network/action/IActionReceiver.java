package net.qbar.common.network.action;

import net.minecraft.nbt.NBTTagCompound;

public interface IActionReceiver
{
    void handle(String actionID, NBTTagCompound payload);
}
