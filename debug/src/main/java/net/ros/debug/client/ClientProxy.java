package net.ros.debug.client;

import net.minecraftforge.common.MinecraftForge;
import net.ros.debug.common.CommonProxy;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(new DebugClientEventHandler());
    }
}
