package net.ros.debug.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "rosdebugmod", name = "Rise Of Steam | Debug Mod", version = "0.1.0")
public class ROSDebugMod
{
    @SidedProxy(clientSide = "net.ros.debug.client.ClientProxy", serverSide = "net.ros.debug.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        proxy.preInit();
    }
}
