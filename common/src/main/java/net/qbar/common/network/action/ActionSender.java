package net.qbar.common.network.action;

import lombok.Data;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

@Data
public class ActionSender
{
    private EntityPlayer player;
    private TileEntity receiver;
    private int actionID;
    private boolean answered;

    public ActionSender(EntityPlayer player, TileEntity receiver, int actionID)
    {
        this.player = player;
        this.receiver = receiver;
        this.actionID = actionID;
    }

    public ClientActionBuilder answer()
    {
        this.answered = true;
        return new ClientActionBuilder(actionID, receiver).toPlayer(player);
    }
}
