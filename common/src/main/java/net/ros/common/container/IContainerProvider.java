package net.ros.common.container;

import net.minecraft.entity.player.EntityPlayer;

public interface IContainerProvider
{
    BuiltContainer createContainer(EntityPlayer player);
}
