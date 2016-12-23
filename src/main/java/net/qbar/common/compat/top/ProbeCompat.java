package net.qbar.common.compat.top;

import java.util.logging.Level;

import mcjty.theoneprobe.TheOneProbe;
import net.qbar.QBar;

public class ProbeCompat
{
    public static final void load()
    {
        QBar.logger.log(Level.INFO, "Compat module for The One Probe is loaded.");
        TheOneProbe.theOneProbeImp.registerProvider(new ProbeProvider());
    }
}