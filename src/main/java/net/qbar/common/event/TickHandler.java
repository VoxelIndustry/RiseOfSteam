package net.qbar.common.event;

import java.util.Queue;

import com.google.common.collect.Queues;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.qbar.common.grid.GridManager;
import net.qbar.common.tile.ILoadable;

public class TickHandler
{
    public static final Queue<ILoadable> loadables = Queues.newArrayDeque();

    @SubscribeEvent
    public void serverTick(final TickEvent.ServerTickEvent e)
    {
        while (TickHandler.loadables.peek() != null)
            TickHandler.loadables.poll().load();

        GridManager.getInstance().tickGrids();
    }
}
