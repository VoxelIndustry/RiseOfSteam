package net.qbar.common.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.qbar.common.steam.ISteamHandler;
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

    // TODO : Use pressure regulation instead of average pressure !
    @Override
    void tick()
    {
        super.tick();

        final Set<ISteamHandler> handlers = this.connectedPipes.stream()
                .flatMap(pipe -> pipe.getConnectedHandlers().stream()).collect(Collectors.toSet());
        handlers.add(this.tank);

        final double average = handlers.stream()
                .mapToDouble(handler -> handler.getPressure() / handler.getMaxPressure()).average().orElse(0);

        final ISteamHandler[] superiors = handlers.stream()
                .filter(handler -> handler.getPressure() / handler.getMaxPressure() - average > 0)
                .toArray(ISteamHandler[]::new);

        final ISteamHandler[] inferiors = handlers.stream()
                .filter(handler -> handler.getPressure() / handler.getMaxPressure() - average < 0)
                .toArray(ISteamHandler[]::new);

        final int drained = Stream.of(superiors).mapToInt(handler ->
        {
            return handler.drainSteam(Math.min(
                    (int) ((handler.getPressure() / handler.getMaxPressure() - average) * handler.getCapacity()),
                    this.transferCapacity), false);
        }).sum();
        int filled = 0;

        for (final ISteamHandler handler : inferiors)
        {
            filled += handler.fillSteam(Math.max(drained / inferiors.length, Math.min(
                    (int) ((handler.getPressure() / handler.getMaxPressure() - average) * handler.getCapacity()),
                    this.transferCapacity)), true);
        }

        for (final ISteamHandler handler : superiors)
        {
            handler.drainSteam(Math.max(filled / superiors.length, Math.min(
                    (int) ((handler.getPressure() / handler.getMaxPressure() - average) * handler.getCapacity()),
                    this.transferCapacity)), true);
        }
    }

    public SteamTank getTank()
    {
        return this.tank;
    }

    public boolean isEmpty()
    {
        return this.tank.getSteam() == 0;
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
