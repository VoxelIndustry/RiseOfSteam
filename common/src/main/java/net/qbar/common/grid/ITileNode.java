package net.qbar.common.grid;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ITileNode<T extends CableGrid>
{
    BlockPos getBlockPos();

    int getGrid();

    void setGrid(int gridIdentifier);

    boolean canConnect(ITileNode<?> to);

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

    BlockPos getAdjacentPos(final int edge);

    default boolean hasGrid()
    {
        return this.getGrid() != -1;
    }

    int[] getConnections();

    ITileNode<T> getConnected(int edge);

    void connect(int edge, ITileNode<T> to);

    void disconnect(int edge);

    int invertEdge(int edge);
}