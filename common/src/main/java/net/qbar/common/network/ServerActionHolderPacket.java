package net.qbar.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.common.QBarConstants;
import net.qbar.common.network.action.ActionSender;
import net.qbar.common.network.action.IActionReceiver;

import java.util.concurrent.atomic.AtomicInteger;

@ReceivedOn(Side.SERVER)
public class ServerActionHolderPacket extends Message
{
    public static AtomicInteger previousActionID = new AtomicInteger();

    private String actionName;

    @Setter
    private NBTTagCompound actionPayload;

    private BlockPos pos;

    @MarshalledAs("i32")
    private int dimension;

    @Getter
    @MarshalledAs("short")
    private int actionID;

    @Setter
    boolean expectAnswer;

    public ServerActionHolderPacket(NetworkContext ctx)
    {
        super(ctx);
    }

    public ServerActionHolderPacket(TileEntity to, String name)
    {
        this(QBarConstants.network);

        this.actionName = name;
        this.dimension = to.getWorld().provider.getDimension();
        this.pos = to.getPos();

        this.actionID = previousActionID.getAndUpdate(previous -> previous > 32765 ? 0 : previous + 1);
    }

    @Override
    protected void handle(EntityPlayer sender)
    {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.dimension);


        if (world.isBlockLoaded(this.pos))
        {
            TileEntity receiver = world.getTileEntity(this.pos);

            if (receiver instanceof IActionReceiver)
            {
                ActionSender actionSender = new ActionSender(sender, receiver, actionID);
                ((IActionReceiver) receiver).handle(actionSender, actionName, actionPayload);
                if (this.expectAnswer && !actionSender.isAnswered())
                    actionSender.answer().send();
            }
        }
    }
}
