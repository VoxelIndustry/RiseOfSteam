package net.qbar.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.init.QBarFluids;
import net.qbar.common.init.QBarItems;

public class CommonProxy
{
    public void preInit(final FMLPreInitializationEvent e)
    {
        QBarBlocks.registerBlocks();
		QBarItems.registerItems();
        QBarFluids.registerFluids();
    }

    public void init(final FMLInitializationEvent e)
    {

    }

    public void postInit(final FMLPostInitializationEvent e)
    {

    }

    public void serverStarted(final FMLServerStartedEvent e)
    {

    }

    public void serverStopping(final FMLServerStoppingEvent e)
    {

    }
}
