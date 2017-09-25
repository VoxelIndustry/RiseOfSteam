package net.qbar.common.grid;

import net.qbar.common.steam.ISteamHandler;

import java.util.Collection;

public interface ISteamPipe extends ITileCable<SteamGrid>
{
    Collection<ISteamHandler> getConnectedHandlers();
}
