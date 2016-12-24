package net.qbar.common.steam;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class SteamUtil
{
    public static final int                 AMBIANT_PRESSURE         = 0;

    @CapabilityInject(ISteamHandler.class)
    public static Capability<ISteamHandler> STEAM_HANDLER_CAPABILITY = null;
}
