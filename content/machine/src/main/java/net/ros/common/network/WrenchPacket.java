package net.ros.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.voxelindustry.steamlayer.network.packet.Message;

public class WrenchPacket extends Message
{
    private BlockPos   pos;
    private EnumFacing facing;

    public WrenchPacket(BlockPos pos, EnumFacing facing)
    {
        this.pos = pos;
        this.facing = facing;
    }

    public WrenchPacket()
    {
    }

    @Override
    public void read(ByteBuf buf)
    {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.facing = EnumFacing.byIndex(buf.readInt());
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeLong(this.pos.toLong());
        buf.writeInt(this.facing.getIndex());
    }

    @Override
    public void handle(EntityPlayer sender)
    {
        for (int i = 0; i < 3; i++)
            sender.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT,
                    pos.getX() + 0.5 + (facing.getXOffset() / 2.0),
                    pos.getY() + 0.5 + (facing.getYOffset() / 2.0),
                    pos.getZ() + 0.5 + (facing.getZOffset() / 2.0),
                    facing.getXOffset() * 0.15f + (sender.getEntityWorld().rand.nextFloat() / 8.0),
                    facing.getYOffset() * 0.15f,
                    facing.getZOffset() * 0.15f + (sender.getEntityWorld().rand.nextFloat() / 8.0));
        if (sender.getEntityWorld().rand.nextFloat() > 0.75f)
            sender.getEntityWorld().playSound(sender, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.06f,
                    sender.world.rand.nextFloat() * 0.1F);
    }
}
