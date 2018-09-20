package net.ros.common.grid;

import net.ros.common.grid.impl.CableGrid;
import net.ros.common.grid.node.ITileNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GridManager
{
    private static volatile GridManager instance;

    public static GridManager getInstance()
    {
        if (GridManager.instance == null)
            synchronized (GridManager.class)
            {
                if (GridManager.instance == null)
                    GridManager.instance = new GridManager();
            }
        return GridManager.instance;
    }

    public Map<Integer, CableGrid> cableGrids;

    private GridManager()
    {
        this.cableGrids = new ConcurrentHashMap<>();
    }

    public CableGrid addGrid(final CableGrid grid)
    {
        if (!this.cableGrids.containsKey(grid.getIdentifier()))
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
        Iterator<CableGrid> gridIterator = this.cableGrids.values().iterator();

        while (gridIterator.hasNext())
        {
            CableGrid next = gridIterator.next();
            if (next.isMarkedForRemoval())
                gridIterator.remove();
            else
                next.tick();
        }
    }

    public <T extends CableGrid> void connectCable(final ITileNode<T> added)
    {
        added.adjacentConnect();

        for (int edge: added.getConnections())
        {
            final ITileNode<T> adjacent = added.getConnected(edge);

            if (adjacent.getGrid() != -1)
            {
                if (added.getGrid() == -1 && this.getGrid(adjacent.getGrid()) != null)
                {
                    added.setGrid(adjacent.getGrid());
                    this.getGrid(adjacent.getGrid()).addCable(added);
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

    public <T extends CableGrid> void disconnectCable(final ITileNode<T> removed)
    {
        if (removed.getGrid() != -1)
        {
            if (removed.getConnections().length != 0)
            {
                for (int edge: removed.getConnections())
                    removed.getConnected(edge).disconnect(removed.invertEdge(edge));

                if (removed.getConnections().length == 1)
                {
                    this.getGrid(removed.getGrid()).removeCable(removed);
                    removed.setGrid(-1);
                }
                else
                {
                    this.getGrid(removed.getGrid()).removeCable(removed);
                    if (!this.getOrphans(this.getGrid(removed.getGrid()), removed).isEmpty())
                    {
                        for (int edge: removed.getConnections())
                            removed.getConnected(edge).setGrid(-1);
                        final CableGrid old = this.removeGrid(removed.getGrid());
                        for (int edge: removed.getConnections())
                        {
                            if (removed.getConnected(edge).getGrid() == -1)
                            {
                                final CableGrid newGrid = this.addGrid(old.copy(this.getNextID()));

                                this.exploreGrid(newGrid, removed.getConnected(edge));
                                newGrid.onSplit(old);
                            }
                        }
                    }
                    removed.setGrid(-1);
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

    public <T extends CableGrid> List<ITileNode<T>> getOrphans(final CableGrid grid, final ITileNode<T> cable)
    {
        final List<ITileNode<T>> toScan = new ArrayList<>();
        // Only here to calm down javac
        grid.getCables().forEach(cable2 -> toScan.add((ITileNode<T>) cable2));

        final List<ITileNode<T>> openset = new ArrayList<>();
        final List<ITileNode<T>> frontier = new ArrayList<>();

        frontier.add(cable.getConnected(cable.getConnections()[0]));
        while (!frontier.isEmpty())
        {
            final List<ITileNode<T>> frontierCpy = new ArrayList<>(frontier);
            for (final ITileNode<T> current: frontierCpy)
            {
                openset.add(current);
                toScan.remove(current);
                for (int edge: current.getConnections())
                {
                    final ITileNode<T> facingCable = current.getConnected(edge);
                    if (!openset.contains(facingCable) && !frontier.contains(facingCable))
                        frontier.add(facingCable);
                }
                frontier.remove(current);
            }
        }
        return toScan;
    }

    private <T extends CableGrid> void exploreGrid(final CableGrid grid, final ITileNode<T> cable)
    {
        final Set<ITileNode<T>> openset = new HashSet<>();
        final Set<ITileNode<T>> frontier = new HashSet<>();

        frontier.add(cable);
        while (!frontier.isEmpty())
        {
            Set<ITileNode<T>> frontierCpy = new HashSet<>(frontier);
            for (final ITileNode<T> current: frontierCpy)
            {
                openset.add(current);
                current.setGrid(grid.getIdentifier());
                grid.addCable(current);
                for (int edge: current.getConnections())
                {
                    ITileNode<T> facingCable = current.getConnected(edge);

                    if (!openset.contains(facingCable))
                        frontier.add(facingCable);
                }
                frontier.remove(current);
            }
        }
    }
}
