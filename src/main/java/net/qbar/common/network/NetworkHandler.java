package net.qbar.common.network;

import com.google.common.base.Predicates;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.qbar.common.tile.QBarTileBase;

public class NetworkHandler
{
    public static void sendTileToPlayer(final QBarTileBase tile, final EntityPlayerMP player)
    {
        if (tile.isServer())
        {
            final SPacketUpdateTileEntity packet = tile.getUpdatePacket();

            if (packet == null)
                return;
            player.connection.sendPacket(packet);
        }
    }

    public static void sendTileToRange(final QBarTileBase tile)
    {
        if (tile.isServer())
        {
            final SPacketUpdateTileEntity packet = tile.getUpdatePacket();

            if (packet == null)
                return;

            final Chunk chunk = tile.getWorld().getChunkFromBlockCoords(tile.getPos());
            if (((WorldServer) tile.getWorld()).getPlayerChunkMap().contains(chunk.xPosition, chunk.zPosition))
            {
                for (final EntityPlayerMP player : tile.getWorld().getPlayers(EntityPlayerMP.class,
                        Predicates.alwaysTrue()))
                {
                    if (((WorldServer) tile.getWorld()).getPlayerChunkMap().isPlayerWatchingChunk(player,
                            chunk.xPosition, chunk.zPosition))
                        player.connection.sendPacket(packet);
                }
            }
        }
    }
}
