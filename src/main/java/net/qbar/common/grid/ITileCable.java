package net.qbar.common.grid;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITileCable<T extends CableGrid>
{
    BlockPos getPos();

    EnumFacing[] getConnections();

    ITileCable<T> getConnected(EnumFacing facing);

    int getGrid();

    void setGrid(int gridIdentifier);

    boolean canConnect(ITileCable<?> to);

    void connect(EnumFacing facing, ITileCable<T> to);

    void disconnect(EnumFacing facing);

    World getWorld();

    T createGrid(int nextID);

    @Nullable
    public default T getGridObject()
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
}
