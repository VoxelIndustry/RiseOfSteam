package net.qbar.common.grid.node;

import com.google.common.collect.LinkedListMultimap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.impl.SteamMachineGrid;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.multiblock.ITileMultiblock;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.steam.ISteamHandler;

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

        Set<BlockPos> openset = new HashSet<>();
        Set<BlockPos> frontier = new HashSet<>();

        frontier.add(this.getBlockPos());
        while (!frontier.isEmpty())
        {
            Set<BlockPos> frontierCpy = new HashSet<>(frontier);
            for (BlockPos current : frontierCpy)
            {
                openset.add(current);

                TileEntity found = this.getBlockWorld().getTileEntity(current);
                if (found == this || (found instanceof ITileMultiblock &&
                        ((ITileMultiblock) found).getCorePos().equals(this.getBlockPos())))
                {
                    for (EnumFacing facing : EnumFacing.VALUES)
                    {
                        BlockPos facingPos = current.offset(facing);
                        if (!openset.contains(facingPos) && !frontier.contains(facingPos))
                            frontier.add(facingPos);
                    }
                }
                else
                {
                    if (found instanceof IModularMachine && ((IModularMachine) found).hasModule(SteamModule.class))
                        toConnect.add(((IModularMachine) found).getModule(SteamModule.class));
                    else if (found instanceof ITileMultiblock && !(found instanceof ITileMultiblockCore) &&
                            !((ITileMultiblock) found).getCorePos().equals(this.getBlockPos()) &&
                            ((ITileMultiblock) found).getCore() instanceof IModularMachine &&
                            ((IModularMachine) ((ITileMultiblock) found).getCore()).hasModule(SteamModule.class))
                        toConnect.add(
                                ((IModularMachine) ((ITileMultiblock) found).getCore()).getModule(SteamModule.class));
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
