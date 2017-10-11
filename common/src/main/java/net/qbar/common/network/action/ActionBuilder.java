package net.qbar.common.network.action;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.network.ActionHolderPacket;

public class ActionBuilder
{
    private String actionKey;

    ActionBuilder(String actionKey)
    {
        this.actionKey = actionKey;
    }

    public ActionBuilder toTile(BlockPos pos)
    {
        return this;
    }

    public ActionHolderPacket send(EntityPlayer player)
    {
        return new ActionHolderPacket();
    }
}
