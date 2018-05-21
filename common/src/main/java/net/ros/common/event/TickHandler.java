package net.ros.common.event;

import com.google.common.collect.Queues;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.ros.common.grid.GridManager;
import net.ros.common.tile.ILoadable;
import net.ros.common.machine.SteamOverloadManager;

import java.util.Queue;

public class TickHandler
{
    public static final Queue<ILoadable> loadables = Queues.newArrayDeque();

    @SubscribeEvent
    public void serverTick(TickEvent.WorldTickEvent e)
    {
        if(e.phase == TickEvent.Phase.START || e.side == Side.CLIENT)
            return;

        while (TickHandler.loadables.peek() != null)
            TickHandler.loadables.poll().load();

        GridManager.getInstance().tickGrids();
        SteamOverloadManager.getInstance().tick();
    }
}
