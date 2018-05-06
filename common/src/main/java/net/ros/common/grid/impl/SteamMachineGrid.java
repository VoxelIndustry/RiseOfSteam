package net.ros.common.grid.impl;

import lombok.Getter;
import net.ros.common.grid.node.ISteamMachine;
import net.ros.common.grid.node.ITileNode;

import javax.annotation.Nonnull;

public class SteamMachineGrid extends CableGrid
{
    @Getter
    private SteamMesh mesh;

    public SteamMachineGrid(int identifier)
    {
        super(identifier);

        this.mesh = new SteamMesh();
    }

    @Override
    public void tick()
    {
        super.tick();

        mesh.tick();
    }

    @Override
    public CableGrid copy(int identifier)
    {
        return new SteamMachineGrid(identifier);
    }

    @Override
    public void addCable(@Nonnull ITileNode<?> cable)
    {
        mesh.addHandler(((ISteamMachine) cable).getInternalSteamHandler());
        super.addCable(cable);
    }

    @Override
    public boolean removeCable(ITileNode<?> cable)
    {
        mesh.removeHandler(((ISteamMachine) cable).getInternalSteamHandler());
        return super.removeCable(cable);
    }
}
