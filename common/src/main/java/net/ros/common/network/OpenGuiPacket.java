package net.ros.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.ros.common.ROSConstants;

@ReceivedOn(Side.SERVER)
public class OpenGuiPacket extends Message
{
    private BlockPos pos;

    @MarshalledAs("i32")
    private int dimension;

    @MarshalledAs("i32")
    private int guiID;

    public OpenGuiPacket(NetworkContext ctx)
    {
        super(ctx);
    }

    public OpenGuiPacket(World w, BlockPos pos, int guiID)
    {
        this(ROSConstants.network);

        this.dimension = w.provider.getDimension();
        this.pos = pos;
        this.guiID = guiID;
    }

    @Override
    protected void handle(EntityPlayer player)
    {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.dimension);

        if (world.isBlockLoaded(this.pos))
        {
            player.openGui(ROSConstants.MODINSTANCE, guiID, world, pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
