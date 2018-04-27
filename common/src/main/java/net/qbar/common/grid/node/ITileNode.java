package net.qbar.common.grid.node;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.impl.CableGrid;

import javax.annotation.Nullable;

public interface ITileNode<T extends CableGrid>
{
    BlockPos getBlockPos();

    int getGrid();

    void setGrid(int gridIdentifier);

    boolean canConnect(int edge, ITileNode<?> to);

    World getBlockWorld();

    T createGrid(int nextID);

    @Nullable
    default T getGridObject()
    {
        try
        {
            final T grid = (T) GridManager.getInstance().getGrid(this.getGrid());

            if (grid != null)
                return grid;
        } catch (final ClassCastException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    default void updateState()
    {

    }

    void adjacentConnect();

    default boolean hasGrid()
    {
        return this.getGrid() != -1;
    }

    int[] getConnections();

    ITileNode<T> getConnected(int edge);

    void disconnect(int edge);

    int invertEdge(int edge);
}