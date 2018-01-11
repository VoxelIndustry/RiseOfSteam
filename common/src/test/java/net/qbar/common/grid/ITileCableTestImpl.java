package net.qbar.common.grid;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumMap;

public class ITileCableTestImpl implements ITileCable
{
    private int                             grid       = -1;

    private EnumMap<EnumFacing, ITileCable> connecteds = new EnumMap<>(EnumFacing.class);

    @Override
    public BlockPos getBlockPos()
    {
        return null;
    }

    @Override
    public int[] getConnections()
    {
        return this.connecteds.keySet().stream().mapToInt(EnumFacing::ordinal).toArray();
    }

    @Override
    public ITileCable<?> getConnected(EnumFacing facing)
    {
        return this.connecteds.get(facing);
    }

    @Override
    public int getGrid()
    {
        return this.grid;
    }

    @Override
    public void setGrid(int gridIdentifier)
    {
        this.grid = gridIdentifier;
    }

    @Override
    public boolean canConnect(ITileNode to)
    {
        return true;
    }

    @Override
    public void connect(EnumFacing facing, ITileCable to)
    {
        this.connecteds.put(facing, to);
    }

    @Override
    public void disconnect(EnumFacing facing)
    {
        this.connecteds.remove(facing);
    }

    @Override
    public World getBlockWorld()
    {
        return null;
    }

    @Override
    public CableGrid createGrid(int nextID)
    {
        return null;
    }

    @Override
    public EnumMap<EnumFacing, ITileCable> getConnectionsMap()
    {
        return this.connecteds;
    }

    @Override
    public void updateState()
    {

    }

    @Override
    public void adjacentConnect()
    {

    }
}
