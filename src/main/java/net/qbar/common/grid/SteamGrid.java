package net.qbar.common.grid;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.qbar.common.steam.SteamTank;

public class SteamGrid extends CableGrid
{
    private int                    transferCapacity;
    private final SteamTank        tank;

    private final List<ISteamPipe> connectedPipes;

    public SteamGrid(final int identifier, final int transferCapacity)
    {
        super(identifier);
        this.transferCapacity = transferCapacity;

        this.connectedPipes = new ArrayList<>();

        this.tank = new SteamTank(0, 0, 1.5f);
    }

    @Override
    void tick()
    {
        super.tick();

        final double average = this.connectedPipes.stream().flatMap(pipe -> pipe.getConnectedHandlers().stream())
                .mapToDouble(handler -> handler.getPressure() / handler.getMaxPressure()).average().orElse(0);

        // System.out.println("Average level in grid is : " + average + " with "
        // + this.connectedPipes.stream().flatMap(pipe ->
        // pipe.getConnectedHandlers().stream()).count()
        // + " elements.");
    }

    public SteamTank getTank()
    {
        return this.tank;
    }

    public boolean isEmpty()
    {
        return this.tank.getAmount() == 0;
    }

    public int getCapacity()
    {
        return this.getCables().size() * this.getTransferCapacity();
    }

    public int getTransferCapacity()
    {
        return this.transferCapacity;
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
    public void addCable(@Nonnull final ITileCable cable)
    {
        super.addCable(cable);
        this.getTank().setCapacity(this.getCapacity());
    }

    @Override
    public boolean removeCable(final ITileCable cable)
    {
        if (super.removeCable(cable))
        {
            this.getTank().setCapacity(this.getCapacity());
            return true;
        }
        return false;
    }
}
