package net.qbar.common.grid;

import java.util.Collection;

import javax.annotation.Nullable;

import net.qbar.common.steam.ISteamHandler;

public interface ISteamPipe extends ITileCable
{
    void supplyNeighbors();

    @Nullable
    public default SteamGrid getGridObject()
    {
        final CableGrid grid = GridManager.getInstance().getGrid(this.getGrid());

        if (grid != null && grid instanceof SteamGrid)
            return (SteamGrid) grid;
        return null;
    }

    Collection<ISteamHandler> getConnectedHandlers();
}
