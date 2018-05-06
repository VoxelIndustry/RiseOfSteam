package net.ros.common.compat.top;

import mcjty.theoneprobe.TheOneProbe;
import net.ros.common.ROSConstants;

public class ProbeCompat
{
    public static final void load()
    {
        ROSConstants.LOGGER.info("Compat module for The One Probe is loaded.");
        TheOneProbe.theOneProbeImp.registerProvider(new ProbeProvider());
    }
}