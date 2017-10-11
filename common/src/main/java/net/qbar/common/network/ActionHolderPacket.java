package net.qbar.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.common.QBarConstants;

@ReceivedOn(Side.SERVER)
public class ActionHolderPacket extends Message
{
    public ActionHolderPacket(NetworkContext ctx)
    {
        super(ctx);
    }

    public ActionHolderPacket()
    {
        this(QBarConstants.network);
    }

    @Override
    protected void handle(EntityPlayer player)
    {

    }
}
