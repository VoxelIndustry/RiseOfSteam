package net.ros.common.grid.node;

import com.google.common.collect.LinkedListMultimap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.steam.ISteamHandler;

/**
 * Unused dummy impl
 * Only here for capability
 */
public class SteamMachine implements ISteamMachine
{
    @Getter
    @Setter
    private int      grid;
    @Getter
    private BlockPos blockPos;
    @Getter
    private World    blockWorld;

    public SteamMachine(BlockPos blockPos, World blockWorld)
    {
        this.blockPos = blockPos;
        this.blockWorld = blockWorld;
    }

    @Override
    public LinkedListMultimap<BlockPos, ISteamMachine> getConnectionsMap()
    {
        return null;
    }

    @Override
    public ISteamHandler getInternalSteamHandler()
    {
        return null;
    }
}
