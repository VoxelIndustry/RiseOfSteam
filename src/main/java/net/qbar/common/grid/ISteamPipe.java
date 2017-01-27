package net.qbar.common.grid;

import java.util.Collection;

import net.qbar.common.steam.ISteamHandler;

public interface ISteamPipe extends ITileCable<SteamGrid>
{
    Collection<ISteamHandler> getConnectedHandlers();
}
