package net.qbar.common.grid.impl;

import lombok.Getter;
import net.qbar.common.grid.node.ISteamPipe;
import net.qbar.common.grid.node.ITileNode;
import net.qbar.common.steam.SteamTank;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class SteamGrid extends CableGrid
{
    private       int       transferCapacity;
    private final SteamTank tank;

    private final Set<ISteamPipe> connectedPipes;

    private double averagePressure;

    public SteamGrid(final int identifier, final int transferCapacity)
    {
        super(identifier);
        this.transferCapacity = transferCapacity;

        this.connectedPipes = new HashSet<>();

        this.tank = new SteamTank(0, this.transferCapacity * 4, 1.5f);
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
        this.getConnectedPipes().addAll(((SteamGrid) grid).getConnectedPipes());
        this.getTank().setCapacity(this.getCapacity());
        if (((SteamGrid) grid).getTank().getSteam() != 0)
            this.getTank().fillInternal(((SteamGrid) grid).getTank().getSteam(), true);
    }

    @Override
    public void onSplit(final CableGrid grid)
    {
        this.getConnectedPipes().addAll(((SteamGrid) grid).getConnectedPipes().stream()
                .filter(this.getCables()::contains).collect(Collectors.toSet()));
        this.getTank()
                .fillInternal(((SteamGrid) grid).getTank().drainInternal(
                        ((SteamGrid) grid).getTank().getSteam() / grid.getCables().size() * this.getCables().size(),
                        false), true);
    }

    @Override
    public void tick()
    {
        super.tick();

        // TODO : Replace with SteamGrid
    }

    public boolean isEmpty()
    {
        return this.tank.getSteam() == 0;
    }

    public int getCapacity()
    {
        return this.getCables().size() * this.getTransferCapacity();
    }

    public void setTransferCapacity(final int transferCapacity)
    {
        this.transferCapacity = transferCapacity;
    }

    public void addConnectedPipe(final ISteamPipe pipe)
    {
        this.connectedPipes.add(pipe);
    }

    public void removeConnectedPipe(final ISteamPipe pipe)
    {
        this.connectedPipes.remove(pipe);
    }

    @Override
    public void addCable(@Nonnull final ITileNode cable)
    {
        super.addCable(cable);
        this.getTank().setCapacity(this.getCapacity());
    }

    @Override
    public boolean removeCable(final ITileNode cable)
    {
        if (super.removeCable(cable))
        {
            this.removeConnectedPipe((ISteamPipe) cable);
            this.getTank().setCapacity(this.getCapacity());

            if (this.getTank().getSteam() > 0)
                this.getTank().drainInternal(this.getTank().getSteam() / (this.getCables().size() + 1), true);
            return true;
        }
        return false;
    }
}
