package net.ros.common.compat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.ros.common.compat.top.ProbeCompat;

public class CompatManager
{
    public static void preInit(final FMLPreInitializationEvent e)
    {

    }

    public static void init(final FMLInitializationEvent e)
    {

    }

    public static void postInit(final FMLPostInitializationEvent e)
    {
        if (Loader.isModLoaded("theoneprobe"))
            ProbeCompat.load();
    }

    public static void serverStarting(final FMLServerStartingEvent e)
    {

    }
}
