package net.ros;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.ros.common.CommonProxy;
import net.ros.common.CustomCreativeTab;
import net.ros.common.ROSConstants;
import net.ros.common.init.ROSBlocks;

@Mod(modid = ROSConstants.MODID, version = ROSConstants.VERSION, name = ROSConstants.MODNAME)
public class RiseOfSteam
{
    static
    {
        FluidRegistry.enableUniversalBucket();
        ROSConstants.TAB_ALL = new CustomCreativeTab("ros", () -> ROSBlocks.BELT);
        ROSConstants.TAB_PIPES = new CustomCreativeTab("ros.pipes", () -> ROSBlocks.STEAM_PIPE_SMALL);
    }

    @SidedProxy(clientSide = "net.ros.client.ClientProxy", serverSide = "net.ros.server.ServerProxy")
    public static CommonProxy proxy;

    @Instance(ROSConstants.MODID)
    public static RiseOfSteam instance;

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event)
    {
        ROSConstants.LOGGER = event.getModLog();
        ROSConstants.MODINSTANCE = instance;
        RiseOfSteam.proxy.preInit(event);
    }

    @EventHandler
    public void init(final FMLInitializationEvent event)
    {
        RiseOfSteam.proxy.init(event);
    }

    @EventHandler
    public void postInit(final FMLPostInitializationEvent event)
    {
        RiseOfSteam.proxy.postInit(event);
    }

    @EventHandler
    public void serverStarted(final FMLServerStartedEvent event)
    {
        RiseOfSteam.proxy.serverStarted(event);
    }

    @EventHandler
    public void serverStopping(final FMLServerStoppingEvent event)
    {
        RiseOfSteam.proxy.serverStopping(event);
    }
}
