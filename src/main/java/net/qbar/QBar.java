package net.qbar;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.qbar.common.CommonProxy;

@Mod(modid = QBar.MODID, version = QBar.VERSION, name = QBar.MODNAME)
public class QBar
{
    public static final String MODID   = "qbar";
    public static final String VERSION = "0.1";
    public static final String MODNAME = "QBar";

    @SidedProxy(clientSide = "net.qbar.client.ClientProxy", serverSide = "net.qbar.server.ServerProxy")
    public static CommonProxy  proxy;

    @Instance(QBar.MODID)
    public static QBar         instance;

    @EventHandler
    public void init(final FMLPreInitializationEvent event)
    {
        QBar.proxy.preInit(event);
    }

    @EventHandler
    public void init(final FMLInitializationEvent event)
    {
        QBar.proxy.init(event);
    }

    @EventHandler
    public void postInit(final FMLPostInitializationEvent event)
    {
        QBar.proxy.postInit(event);
    }

    @EventHandler
    public void serverStarted(final FMLServerStartedEvent event)
    {
        QBar.proxy.serverStarted(event);
    }

    @EventHandler
    public void serverStopping(final FMLServerStoppingEvent event)
    {
        QBar.proxy.serverStopping(event);
    }
}
