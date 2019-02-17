package net.ros.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.ros.common.ROSConstants;
import net.voxelindustry.steamlayer.network.packet.Message;

public class OpenGuiPacket extends Message
{
    private BlockPos pos;
    private int      dimension;

    private int guiID;

    public OpenGuiPacket(World w, BlockPos pos, int guiID)
    {
        this.dimension = w.provider.getDimension();
        this.pos = pos;
        this.guiID = guiID;
    }

    public OpenGuiPacket()
    {
    }

    @Override
    public void read(ByteBuf buf)
    {
        this.dimension = buf.readInt();
        this.pos = BlockPos.fromLong(buf.readLong());
        this.guiID = buf.readInt();
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeInt(this.dimension);
        buf.writeLong(this.pos.toLong());
        buf.writeInt(this.guiID);
    }

    @Override
    public void handle(EntityPlayer player)
    {
        World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.dimension);

        if (world.isBlockLoaded(this.pos))
            player.openGui(ROSConstants.MODINSTANCE, guiID, world, pos.getX(), pos.getY(), pos.getZ());
    }
}