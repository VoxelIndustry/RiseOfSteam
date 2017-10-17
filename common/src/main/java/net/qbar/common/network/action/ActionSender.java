package net.qbar.common.network.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

@Data
@AllArgsConstructor
public class ActionSender
{
    private EntityPlayer player;
    private TileEntity receiver;
    private short actionID;

    public ClientActionBuilder answer()
    {
        return new ClientActionBuilder(actionID, receiver).toPlayer(player);
    }
}
