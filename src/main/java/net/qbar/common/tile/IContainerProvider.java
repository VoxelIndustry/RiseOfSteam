package net.qbar.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.qbar.common.container.BuiltContainer;

public interface IContainerProvider
{
    public BuiltContainer createContainer(EntityPlayer player);
}
