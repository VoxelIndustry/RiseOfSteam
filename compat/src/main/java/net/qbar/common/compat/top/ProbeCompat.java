package net.qbar.common.compat.top;

import mcjty.theoneprobe.TheOneProbe;
import net.qbar.common.QBarConstants;

public class ProbeCompat
{
    public static final void load()
    {
        QBarConstants.LOGGER.info("Compat module for The One Probe is loaded.");
        TheOneProbe.theOneProbeImp.registerProvider(new ProbeProvider());
    }
}