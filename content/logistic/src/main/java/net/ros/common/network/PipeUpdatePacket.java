package net.ros.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.ros.common.tile.TilePipeBase;
import net.voxelindustry.steamlayer.network.packet.Message;

import java.util.ArrayList;
import java.util.List;

public class PipeUpdatePacket extends Message
{
    private NBTTagCompound sourceTag;
    private List<NBTTagCompound> adjacentsTag;

    private BlockPos sourcePos;
    private List<BlockPos> adjacentsPos;

    public PipeUpdatePacket(TilePipeBase source, List<TilePipeBase> adjacents)
    {
        this.sourcePos = source.getPos();
        this.sourceTag = source.writeToNBT(new NBTTagCompound());

        this.adjacentsPos = new ArrayList<>();
        this.adjacentsTag = new ArrayList<>();
        for (TilePipeBase adjacent : adjacents)
        {
            this.adjacentsPos.add(adjacent.getPos());
            this.adjacentsTag.add(adjacent.writeRenderConnections(new NBTTagCompound()));
        }
    }

    public PipeUpdatePacket()
    {
    }

    @Override
    public void read(ByteBuf buf)
    {
        sourceTag = ByteBufUtils.readTag(buf);
        sourcePos = BlockPos.fromLong(buf.readLong());

        int tagCount = buf.readInt();

        for (int i = 0; i < tagCount; i++)
            adjacentsTag.add(ByteBufUtils.readTag(buf));

        int posCount = buf.readInt();

        for (int i = 0; i < posCount; i++)
            adjacentsPos.add(BlockPos.fromLong(buf.readLong()));
    }

    @Override
    public void write(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, sourceTag);
        buf.writeLong(sourcePos.toLong());

        buf.writeInt(adjacentsTag.size());

        adjacentsTag.forEach(tag -> ByteBufUtils.writeTag(buf, tag));

        buf.writeInt(adjacentsPos.size());

        adjacentsPos.forEach(pos -> buf.writeLong(pos.toLong()));
    }

    @Override
    public void handle(EntityPlayer sender)
    {
        World w = sender.getEntityWorld();

        if (!w.isBlockLoaded(sourcePos))
            return;
        TilePipeBase source = (TilePipeBase) w.getTileEntity(sourcePos);

        if (source != null)
            source.readRenderConnections(sourceTag);

        int i = 0;
        for (BlockPos posAdjacent : this.adjacentsPos)
        {
            if (w.getTileEntity(posAdjacent) != null)
                ((TilePipeBase) w.getTileEntity(posAdjacent)).readRenderConnections(this.adjacentsTag.get(i));
            i++;
        }

        w.markBlockRangeForRenderUpdate(sourcePos.add(-1, -1, -1), sourcePos.add(1, 1, 1));
    }
}
