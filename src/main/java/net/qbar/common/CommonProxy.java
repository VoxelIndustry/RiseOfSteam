package net.qbar.common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.qbar.common.compat.CompatManager;
import net.qbar.common.event.TickHandler;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.init.QBarFluids;
import net.qbar.common.init.QBarItems;
import net.qbar.common.steam.CapabilitySteamHandler;

public class CommonProxy
{
    public void preInit(final FMLPreInitializationEvent e)
    {
        CapabilitySteamHandler.register();

        QBarBlocks.registerBlocks();
        QBarItems.registerItems();
        QBarFluids.registerFluids();

        MinecraftForge.EVENT_BUS.register(new TickHandler());
        CompatManager.preInit(e);
    }

    public void init(final FMLInitializationEvent e)
    {
        CompatManager.init(e);
    }

    public void postInit(final FMLPostInitializationEvent e)
    {
        CompatManager.postInit(e);
    }

    public void serverStarted(final FMLServerStartedEvent e)
    {
    }

    public void serverStopping(final FMLServerStoppingEvent e)
    {

    }
}
