package net.ros.common.network.action;

import net.minecraft.nbt.NBTTagCompound;

public interface IActionReceiver
{
    void handle(ActionSender sender, String actionID, NBTTagCompound payload);
}
