package net.ros.common.grid.node;

import net.ros.common.grid.impl.SteamGrid;
import net.ros.common.steam.ISteamHandler;

import java.util.Collection;

public interface ISteamPipe extends IPipe<SteamGrid>
{
    Collection<ISteamHandler> getConnectedHandlers();
}
