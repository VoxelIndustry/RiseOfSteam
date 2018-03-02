package net.qbar.common.grid.node;

import net.qbar.common.grid.impl.SteamGrid;
import net.qbar.common.grid.node.ITileCable;
import net.qbar.common.steam.ISteamHandler;

import java.util.Collection;

public interface ISteamPipe extends ITileCable<SteamGrid>
{
    Collection<ISteamHandler> getConnectedHandlers();
}
