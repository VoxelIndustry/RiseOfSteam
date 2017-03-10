package net.qbar;

import java.util.logging.Logger;

import com.elytradev.concrete.NetworkContext;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fluids.FluidRegistry;
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
import net.qbar.common.CustomCreativeTab;

@Mod(modid = QBar.MODID, version = QBar.VERSION, name = QBar.MODNAME)
public class QBar
{
    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    public static final String       MODID   = "qbar";
    public static final String       VERSION = "0.1";
    public static final String       MODNAME = "QBar";

    @SidedProxy(clientSide = "net.qbar.client.ClientProxy", serverSide = "net.qbar.server.ServerProxy")
    public static CommonProxy        proxy;

    @Instance(QBar.MODID)
    public static QBar               instance;

    public static final Logger       logger  = Logger.getLogger(QBar.MODNAME);

    public static final CreativeTabs TAB_ALL = new CustomCreativeTab("QBar");

    public static NetworkContext     network;

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event)
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
