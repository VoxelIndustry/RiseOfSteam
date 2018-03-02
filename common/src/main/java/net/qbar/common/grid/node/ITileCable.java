package net.qbar.common.grid.node;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.grid.impl.CableGrid;

import java.util.EnumMap;

public interface ITileCable<T extends CableGrid> extends ITileNode<T>
{
    EnumMap<EnumFacing, ITileCable<T>> getConnectionsMap();

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

    default int[] getConnections()
    {
        return this.getConnectionsMap().keySet().stream().mapToInt(EnumFacing::ordinal).toArray();
    }

    default ITileCable<T> getConnected(int edge)
    {
        return this.getConnected(EnumFacing.VALUES[edge]);
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

    @Override
    default void disconnect(int edge)
    {
        this.disconnect(EnumFacing.VALUES[edge]);
    }

    @Override
    default int invertEdge(int edge)
    {
        return EnumFacing.VALUES[edge].getOpposite().ordinal();
    }
}
