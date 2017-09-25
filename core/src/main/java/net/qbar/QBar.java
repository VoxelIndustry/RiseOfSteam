package net.qbar;

import com.elytradev.concrete.network.NetworkContext;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.qbar.common.CommonProxy;
import net.qbar.common.CustomCreativeTab;
import net.qbar.common.QBarConstants;
import org.apache.logging.log4j.Logger;

@Mod(modid = QBarConstants.MODID, version = QBarConstants.VERSION, name = QBarConstants.MODNAME)
public class QBar
{
    static
    {
        FluidRegistry.enableUniversalBucket();
        QBarConstants.TAB_ALL = new CustomCreativeTab("QBar");
    }

    @SidedProxy(clientSide = "net.qbar.client.ClientProxy", serverSide = "net.qbar.server.ServerProxy")
    public static CommonProxy proxy;

    @Instance(QBarConstants.MODID)
    public static QBar instance;

    public static Logger logger;

    public static NetworkContext network;

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
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
