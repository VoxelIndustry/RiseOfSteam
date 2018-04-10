package net.qbar.common.grid.node;

import com.google.common.collect.LinkedListMultimap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.impl.SteamMachineGrid;
import net.qbar.common.multiblock.ITileMultiblock;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.steam.SteamCapabilities;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public interface ISteamMachine extends ITileNode<SteamMachineGrid>
{
    LinkedListMultimap<BlockPos, ISteamMachine> getConnectionsMap();

    ISteamHandler getInternalSteamHandler();

    default SteamMachineGrid createGrid(int nextID)
    {
        return new SteamMachineGrid(nextID);
    }

    default void disconnectItself()
    {
        GridManager.getInstance().disconnectCable(this);
    }

    default void load()
    {
        GridManager.getInstance().connectCable(this);
    }

    @Override
    default boolean canConnect(ITileNode<?> to)
    {
        return to instanceof ISteamMachine;
    }

    @Override
    default void adjacentConnect()
    {
        Set<ISteamMachine> toConnect = new HashSet<>();

        Set<MultiblockSide> openset = new HashSet<>();
        Set<MultiblockSide> frontier = new HashSet<>();

        frontier.add(new MultiblockSide(this.getBlockPos(), EnumFacing.NORTH));
        while (!frontier.isEmpty())
        {
            Set<MultiblockSide> frontierCpy = new HashSet<>(frontier);
            for (MultiblockSide current : frontierCpy)
            {
                openset.add(current);

                TileEntity found = this.getBlockWorld().getTileEntity(current.getPos());
                if (found == this || (found instanceof ITileMultiblock &&
                        ((ITileMultiblock) found).getCorePos().equals(this.getBlockPos())))
                {
                    for (EnumFacing facing : EnumFacing.VALUES)
                    {
                        BlockPos facingPos = current.getPos().offset(facing);
                        if (!openset.contains(facingPos) && !frontier.contains(new MultiblockSide(facingPos, facing)))
                            frontier.add(new MultiblockSide(facingPos, facing));
                    }
                }
                else
                {
                    if (found != null && found.hasCapability(SteamCapabilities.STEAM_MACHINE,
                            current.getFacing().getOpposite()))
                        toConnect.add(found.getCapability(SteamCapabilities.STEAM_MACHINE,
                                current.getFacing().getOpposite()));
                }
                frontier.remove(current);
            }
        }

        toConnect.forEach(tile ->
        {
            if (this.canConnect(tile) && tile.canConnect(this))
            {
                this.connect(tile.getBlockPos(), tile);
                tile.connect(this.getBlockPos(), this);
            }
        });
    }

    @Override
    default int invertEdge(int edge)
    {
        return this.getConnected(edge).getConnectionsMap().values().indexOf(
                this.getConnected(edge).getConnectionsMap().get(this.getBlockPos()).get(0));
    }

    default int[] getConnections()
    {
        return IntStream.range(0, this.getConnectionsMap().size()).toArray();
    }

    default ISteamMachine getConnected(int edge)
    {
        return this.getConnectionsMap().values().get(edge);
    }

    default void connect(BlockPos pos, ISteamMachine to)
    {
        if (this.getConnectionsMap().containsKey(pos))
            this.getConnectionsMap().get(pos).clear();
        this.getConnectionsMap().put(pos, to);
    }

    @Override
    default void disconnect(int edge)
    {
        this.getConnectionsMap().remove(this.getConnectionsMap().entries().get(edge).getKey(), this.getConnected(edge));
    }
}
