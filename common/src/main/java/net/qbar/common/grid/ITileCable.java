package net.qbar.common.grid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumMap;

public interface ITileCable<T extends CableGrid>
{
    BlockPos getBlockPos();

    int getGrid();

    void setGrid(int gridIdentifier);

    boolean canConnect(ITileCable<?> to);

    World getBlockWorld();

    T createGrid(int nextID);

    EnumMap<EnumFacing, ITileCable<T>> getConnectionsMap();

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

    default void adjacentConnect()
    {
        for (final EnumFacing facing : EnumFacing.VALUES)
        {
            final TileEntity adjacent = this.getBlockWorld().getTileEntity(this.getAdjacentPos(facing));
            if (adjacent != null && adjacent instanceof ITileCable && this.canConnect((ITileCable<?>) adjacent)
                    && ((ITileCable<?>) adjacent).canConnect(this))
            {
                this.connect(facing, (ITileCable<T>) adjacent);
                ((ITileCable<T>) adjacent).connect(facing.getOpposite(), this);
            }
        }
    }

    default BlockPos getAdjacentPos(final EnumFacing facing)
    {
        return this.getBlockPos().offset(facing);
    }

    default boolean hasGrid()
    {
        return this.getGrid() != -1;
    }

    default EnumFacing[] getConnections()
    {
        return this.getConnectionsMap().keySet().toArray(new EnumFacing[0]);
    }

    default ITileCable<T> getConnected(EnumFacing facing)
    {
        return this.getConnectionsMap().get(facing);
    }

    default void connect(EnumFacing facing, ITileCable<T> to)
    {
        this.getConnectionsMap().put(facing, to);
    }

    default void disconnect(EnumFacing facing)
    {
        this.getConnectionsMap().remove(facing);
    }
}
