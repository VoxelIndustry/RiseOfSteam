package net.qbar.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.common.QBarConstants;
import net.qbar.common.network.action.ActionManager;

import java.util.concurrent.atomic.AtomicInteger;

@ReceivedOn(Side.CLIENT)
public class ClientActionHolderPacket extends Message
{
    public static AtomicInteger previousActionID;

    private NBTTagCompound actionPayload;

    private BlockPos pos;

    @MarshalledAs("i32")
    private int dimension;

    @MarshalledAs("short")
    private int replyID;

    public ClientActionHolderPacket(NetworkContext ctx)
    {
        super(ctx);
    }

    public ClientActionHolderPacket(int replyID, TileEntity from, NBTTagCompound payload)
    {
        this(QBarConstants.network);

        this.actionPayload = payload;
        this.dimension = from.getWorld().provider.getDimension();
        this.pos = from.getPos();
        this.replyID = replyID;
    }

    @Override
    protected void handle(EntityPlayer receiver)
    {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.dimension);

        ActionManager.getInstance().triggerCallback(replyID, actionPayload);
    }
}
