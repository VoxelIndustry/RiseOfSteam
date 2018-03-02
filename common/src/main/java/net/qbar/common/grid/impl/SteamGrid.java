package net.qbar.common.grid.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import net.qbar.common.grid.node.ISteamPipe;
import net.qbar.common.grid.node.ITileNode;
import net.qbar.common.steam.ISteamHandler;

@Getter
public class SteamGrid extends CableGrid
{
    private int transferCapacity;

    private final ListMultimap<ISteamHandler, ISteamPipe> handlersConnections;

    private SteamMesh mesh;

    public SteamGrid(final int identifier, final int transferCapacity)
    {
        super(identifier);
        this.transferCapacity = transferCapacity;

        this.handlersConnections = MultimapBuilder.hashKeys().arrayListValues().build();
        this.mesh = new SteamMesh(transferCapacity);
    }

    @Override
    public CableGrid copy(final int identifier)
    {
        return new SteamGrid(identifier, this.transferCapacity);
    }

    @Override
    public boolean canMerge(final CableGrid grid)
    {
        if (grid instanceof SteamGrid && ((SteamGrid) grid).getTransferCapacity() == this.transferCapacity)
            return super.canMerge(grid);
        return false;
    }

    @Override
    public void onMerge(final CableGrid grid)
    {
        this.handlersConnections.putAll(((SteamGrid) grid).handlersConnections);
    }

    @Override
    public void onSplit(final CableGrid grid)
    {
        ((SteamGrid) grid).handlersConnections.forEach((handler, pipe) ->
        {
            if (this.hasCable(pipe))
                this.handlersConnections.put(handler, pipe);
        });
    }

    @Override
    public void tick()
    {
        super.tick();

        this.mesh.tick();
    }

    public int getCapacity()
    {
        return this.getCables().size() * this.getTransferCapacity();
    }

    public void addConnectedPipe(final ISteamPipe pipe, final ISteamHandler handler)
    {
        if (!this.handlersConnections.containsKey(handler))
            this.mesh.addHandler(handler);
        if (!this.handlersConnections.containsEntry(handler, pipe))
            this.handlersConnections.put(handler, pipe);
    }

    private void clearConnectedPipe(final ISteamPipe pipe)
    {
        pipe.getConnectedHandlers().forEach(handler -> this.removeConnectedPipe(pipe, handler));
    }

    public void removeConnectedPipe(final ISteamPipe pipe, final ISteamHandler handler)
    {
        this.handlersConnections.remove(handler, pipe);

        if (!this.handlersConnections.containsKey(handler))
            this.mesh.removeHandler(handler);
    }

    @Override
    public boolean removeCable(final ITileNode cable)
    {
        if (super.removeCable(cable))
        {
            this.clearConnectedPipe((ISteamPipe) cable);
            return true;
        }
        return false;
    }

    public ISteamHandler getTank()
    {
        return this.mesh;
    }
}
