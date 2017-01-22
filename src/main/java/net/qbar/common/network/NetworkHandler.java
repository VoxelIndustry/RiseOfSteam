package net.qbar.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.WorldServer;
import net.qbar.common.tile.QBarTileBase;

public class NetworkHandler
{
    public static void sendTileToRange(final QBarTileBase tile)
    {
        if (tile.isServer())
        {
            final SPacketUpdateTileEntity packet = tile.getUpdatePacket();

            if (packet == null)
                return;

            for (final EntityPlayer player : tile.getWorld().playerEntities)
            {
                if (player.getDistanceSq(tile.getPos()) < 64 * 64
                        && ((WorldServer) tile.getWorld()).getPlayerChunkMap().isPlayerWatchingChunk(
                                (EntityPlayerMP) player, tile.getPos().getX() >> 4, tile.getPos().getZ() >> 4))
                    ((EntityPlayerMP) player).connection.sendPacket(packet);
            }
        }
    }
}
