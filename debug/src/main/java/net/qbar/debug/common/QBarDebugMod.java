package net.qbar.debug.common;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "qbardebugmod", name = "QBar | Debug Mod", version = "0.1.0")
public class QBarDebugMod
{
    @SidedProxy(clientSide = "net.qbar.debug.client.ClientProxy", serverSide = "net.qbar.debug.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        proxy.preInit();
    }
}
