package net.ros.common.compat.top;

import mcjty.theoneprobe.TheOneProbe;
import net.ros.common.ROSConstants;

public class ProbeCompat
{
    static int ELEMENT_FLUID;

    public static void load()
    {
        ROSConstants.LOGGER.info("Compat module for The One Probe is loaded.");

        ELEMENT_FLUID = TheOneProbe.theOneProbeImp.registerElementFactory(FluidElement::new);

        TheOneProbe.theOneProbeImp.registerProvider(new ProbeProvider());
    }
}