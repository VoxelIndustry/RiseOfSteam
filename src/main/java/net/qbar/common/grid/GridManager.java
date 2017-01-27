package net.qbar.common.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class GridManager
{
    private static volatile GridManager instance;

    public static final GridManager getInstance()
    {
        if (GridManager.instance == null)
            synchronized (GridManager.class)
            {
                if (GridManager.instance == null)
                    GridManager.instance = new GridManager();
            }
        return GridManager.instance;
    }

    public HashMap<Integer, CableGrid> cableGrids;

    private GridManager()
    {
        this.cableGrids = new HashMap<>();
    }

    public CableGrid addGrid(final CableGrid grid)
    {
        this.cableGrids.put(grid.getIdentifier(), grid);
        return grid;
    }

    public CableGrid removeGrid(final int identifier)
    {
        return this.cableGrids.remove(identifier);
    }

    public boolean hasGrid(final int identifier)
    {
        return this.cableGrids.containsKey(identifier);
    }

    public CableGrid getGrid(final int identifier)
    {
        if (this.cableGrids.containsKey(identifier))
            return this.cableGrids.get(identifier);
        return null;
    }

    public int getNextID()
    {
        int i = 0;
        while (this.cableGrids.containsKey(i))
            i++;
        return i;
    }

    public void tickGrids()
    {
        final Iterator<CableGrid> cableGrid = this.cableGrids.values().iterator();

        while (cableGrid.hasNext())
            cableGrid.next().tick();
    }

    public void connectCable(final ITileCable added)
    {
        for (final EnumFacing facing : EnumFacing.VALUES)
        {

            final TileEntity adjacent = added.getWorld().getTileEntity(added.getPos().add(facing.getDirectionVec()));
            if (adjacent != null && adjacent instanceof ITileCable && added.canConnect((ITileCable) adjacent))
            {
                added.connect(facing, (ITileCable) adjacent);
                ((ITileCable) adjacent).connect(facing.getOpposite(), added);
            }
        }
        for (final EnumFacing facing : added.getConnections())
        {
            final ITileCable adjacent = added.getConnected(facing);

            if (adjacent.getGrid() != -1)
            {
                if (added.getGrid() == -1 && added.canConnect(adjacent))
                {
                    added.setGrid(adjacent.getGrid());
                    this.getGrid(added.getGrid()).addCable(added);
                }
                else if (this.getGrid(added.getGrid()).canMerge(this.getGrid(adjacent.getGrid())))
                    this.mergeGrids(this.getGrid(added.getGrid()), this.getGrid(adjacent.getGrid()));
            }
        }

        if (added.getGrid() == -1)
        {

            final CableGrid newGrid = this.addGrid(added.createGrid(this.getNextID()));
            newGrid.addCable(added);
            added.setGrid(newGrid.getIdentifier());
        }
    }

    public void disconnectCable(final ITileCable removed)
    {
        if (removed.getGrid() != -1)
        {
            if (removed.getConnections().length != 0)
            {
                for (final EnumFacing facing : removed.getConnections())
                    removed.getConnected(facing).disconnect(facing.getOpposite());

                if (removed.getConnections().length == 1)
                    this.getGrid(removed.getGrid()).removeCable(removed);
                else
                {
                    this.getGrid(removed.getGrid()).removeCable(removed);
                    if (!this.getOrphans(this.getGrid(removed.getGrid()), removed).isEmpty())
                    {
                        for (final EnumFacing facing : removed.getConnections())
                            removed.getConnected(facing).setGrid(-1);
                        final CableGrid old = this.removeGrid(removed.getGrid());
                        for (final EnumFacing facing : removed.getConnections())
                        {
                            if (removed.getConnected(facing).getGrid() == -1)
                            {
                                final CableGrid newGrid = this.addGrid(old.copy(this.getNextID()));

                                this.exploreGrid(newGrid, removed.getConnected(facing));
                                newGrid.onSplit(old);
                            }
                        }
                    }
                }
            }
            else
                this.removeGrid(removed.getGrid());
        }
    }

    public void mergeGrids(final CableGrid destination, final CableGrid source)
    {
        destination.addCables(source.getCables());

        source.getCables().forEach(cable -> cable.setGrid(destination.getIdentifier()));
        this.cableGrids.remove(source.getIdentifier());
        destination.onMerge(source);
    }

    List<ITileCable> getOrphans(final CableGrid grid, final ITileCable cable)
    {
        final List<ITileCable> toScan = new ArrayList<>(grid.getCables());

        final List<ITileCable> openset = new ArrayList<>();
        final List<ITileCable> frontier = new ArrayList<>();

        frontier.add(cable.getConnected(cable.getConnections()[0]));
        while (!frontier.isEmpty())
        {
            final List<ITileCable> frontierCpy = new ArrayList<>(frontier);
            for (final ITileCable current : frontierCpy)
            {

                openset.add(current);
                toScan.remove(current);
                for (final EnumFacing facing : current.getConnections())
                {
                    final ITileCable facingCable = current.getConnected(facing);
                    if (!openset.contains(facingCable) && !frontier.contains(facingCable))
                        frontier.add(facingCable);
                }
                frontier.remove(current);
            }
        }
        return toScan;
    }

    private void exploreGrid(final CableGrid grid, final ITileCable cable)
    {
        final List<ITileCable> openset = new ArrayList<>();

        final List<ITileCable> frontier = new ArrayList<>();

        frontier.add(cable);
        while (!frontier.isEmpty())
        {
            final List<ITileCable> frontierCpy = new ArrayList<>(frontier);
            for (final ITileCable current : frontierCpy)
            {

                openset.add(current);
                current.setGrid(grid.getIdentifier());
                grid.addCable(current);
                for (final EnumFacing facing : current.getConnections())
                {
                    final ITileCable facingCable = current.getConnected(facing);
                    if (!openset.contains(facingCable) && !frontier.contains(facingCable))
                        frontier.add(facingCable);
                }
                frontier.remove(current);
            }
        }
    }
}
