package net.ros.common.compat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.ros.common.compat.top.ProbeCompat;
import net.ros.common.compat.top.ProbeCompat;

public class CompatManager
{
    public static final void preInit(final FMLPreInitializationEvent e)
    {

    }

    public static final void init(final FMLInitializationEvent e)
    {

    }

    public static final void postInit(final FMLPostInitializationEvent e)
    {
        if (Loader.isModLoaded("theoneprobe"))
            ProbeCompat.load();
    }

    public static final void serverStarting(final FMLServerStartingEvent e)
    {

    }
}