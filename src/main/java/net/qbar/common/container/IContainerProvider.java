package net.qbar.common.container;

import net.minecraft.entity.player.EntityPlayer;

public interface IContainerProvider
{
    public BuiltContainer createContainer(EntityPlayer player);
}
