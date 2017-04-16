package net.qbar.client;

import java.lang.ref.WeakReference;
import java.util.Queue;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.qbar.common.util.UniqueArrayDeque;

public class ClientTickHandler
{
    public static final Queue<Chunk> scheduledRender = new UniqueArrayDeque<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            while (ClientTickHandler.scheduledRender.peek() != null)
            {
                Chunk chunk = scheduledRender.poll();
                if (chunk != null)
                {
                    chunk.getWorld().markBlockRangeForRenderUpdate(chunk.getPos().getXStart(), 64,
                            chunk.getPos().getZStart(), chunk.getPos().getXEnd(), 64, chunk.getPos().getZEnd());
                }
            }
        }
    }
}
