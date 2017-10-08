package net.qbar.client;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.qbar.common.util.UniqueArrayDeque;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Queue;

public class ClientTickHandler
{
    public static final Queue<Pair<Chunk, Integer>> scheduledRender = new UniqueArrayDeque<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            while (ClientTickHandler.scheduledRender.peek() != null)
            {
                Pair<Chunk, Integer> chunkPair = scheduledRender.poll();
                if (chunkPair.getKey() != null)
                {
                    chunkPair.getKey().getWorld().markBlockRangeForRenderUpdate(chunkPair.getKey().getPos().getXStart(),
                            chunkPair.getValue(), chunkPair.getKey().getPos().getZStart(),
                            chunkPair.getKey().getPos().getXEnd(), chunkPair.getValue(),
                            chunkPair.getKey().getPos().getZEnd());
                }
            }
        }
    }
}
