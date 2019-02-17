package net.ros.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.client.fx.SteamParticleHelper;
import net.voxelindustry.steamlayer.network.packet.Message;

public class SteamEffectPacket extends Message
{
    private int      dimension;
    private BlockPos pos;
    private BlockPos target;
    private boolean  small;

    public SteamEffectPacket(World w, BlockPos pos, BlockPos target, boolean small)
    {
        this.dimension = w.provider.getDimension();
        this.pos = pos;
        this.target = target;
        this.small = small;
    }

    public SteamEffectPacket()
    {
    }

    @Override
    public void read(ByteBuf buf)
    {
        dimension = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
        target = BlockPos.fromLong(buf.readLong());
        small = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeInt(dimension);
        buf.writeLong(pos.toLong());
        buf.writeLong(target.toLong());
        buf.writeBoolean(small);
    }

    @Override
    public void handle(EntityPlayer player)
    {
        if (Minecraft.getMinecraft().player.getEntityWorld().provider.getDimension() != dimension)
            return;

        World world = Minecraft.getMinecraft().player.getEntityWorld();

        if (world.isBlockLoaded(pos))
            SteamParticleHelper.createSmallSteamJet(world, pos, target, small);
    }
}