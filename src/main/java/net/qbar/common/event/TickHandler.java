package net.qbar.common.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.qbar.common.grid.GridManager;

public class TickHandler
{
    @SubscribeEvent
    public void serverTick(final TickEvent.ServerTickEvent e)
    {
        GridManager.getInstance().tickGrids();
    }
}
