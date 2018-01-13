package net.qbar.debug.client;

import net.minecraftforge.common.MinecraftForge;
import net.qbar.debug.common.CommonProxy;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(new DebugClientEventHandler());
    }
}
