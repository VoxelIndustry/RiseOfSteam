package net.qbar.common.grid;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ITileCableTestImpl implements ITileCable
{
    private int grid = -1;

    @Override
    public BlockPos getBlockPos()
    {
        return null;
    }

    @Override
    public EnumFacing[] getConnections()
    {
        return new EnumFacing[0];
    }

    @Override
    public ITileCable<?> getConnected(EnumFacing facing)
    {
        return null;
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
    public boolean canConnect(ITileCable to)
    {
        return true;
    }

    @Override
    public void connect(EnumFacing facing, ITileCable to)
    {

    }

    @Override
    public void disconnect(EnumFacing facing)
    {

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
    public void updateState()
    {

    }

    @Override
    public void adjacentConnect()
    {

    }
}
