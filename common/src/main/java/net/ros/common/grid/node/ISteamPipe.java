package net.ros.common.grid.node;

import net.ros.common.grid.impl.SteamGrid;
import net.ros.common.steam.ISteamHandler;
import net.ros.common.steam.SteamTank;

import java.util.Collection;

public interface ISteamPipe extends IPipe<SteamGrid>
{
    Collection<ISteamHandler> getConnectedHandlers();

    SteamTank getBufferTank();

    int getTransferCapacity();
}
