package net.qbar.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.QBar;
import net.qbar.common.tile.TilePipeBase;

import java.util.ArrayList;
import java.util.List;

@ReceivedOn(Side.CLIENT)
public class PipeUpdatePacket extends Message
{
    @MarshalledAs("nbt")
    NBTTagCompound       sourceTag;

    @MarshalledAs("nbt-list")
    List<NBTTagCompound> adjacentsTag;

    @MarshalledAs("blockpos")
    BlockPos             sourcePos;

    @MarshalledAs("blockpos-list")
    List<BlockPos>       adjacentsPos;

    public PipeUpdatePacket(final NetworkContext ctx)
    {
        super(ctx);
    }

    public PipeUpdatePacket(TilePipeBase source, List<TilePipeBase> adjacents)
    {
        super(QBar.network);

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

    @Override
    protected void handle(EntityPlayer sender)
    {
        World w = sender.getEntityWorld();

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
