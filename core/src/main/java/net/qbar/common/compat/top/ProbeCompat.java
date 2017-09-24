package net.qbar.common.compat.top;

import mcjty.theoneprobe.TheOneProbe;
import net.qbar.QBar;

import java.util.logging.Level;

public class ProbeCompat
{
    public static final void load()
    {
        QBar.logger.info("Compat module for The One Probe is loaded.");
        TheOneProbe.theOneProbeImp.registerProvider(new ProbeProvider());
    }
}