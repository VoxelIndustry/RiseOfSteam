package net.qbar.common.grid;

import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.steam.SteamTank;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SteamGrid extends CableGrid
{
    private       int       transferCapacity;
    private final SteamTank tank;

    private final Set<ISteamPipe> connectedPipes;

    public SteamGrid(final int identifier, final int transferCapacity)
    {
        super(identifier);
        this.transferCapacity = transferCapacity;

        this.connectedPipes = new HashSet<>();

        this.tank = new SteamTank(0, this.transferCapacity * 4, 1.5f);
    }

    @Override
    CableGrid copy(final int identifier)
    {
        return new SteamGrid(identifier, this.transferCapacity);
    }

    @Override
    boolean canMerge(final CableGrid grid)
    {
        if (grid instanceof SteamGrid && ((SteamGrid) grid).getTransferCapacity() == this.transferCapacity)
            return super.canMerge(grid);
        return false;
    }

    @Override
    void onMerge(final CableGrid grid)
    {
        this.getConnectedPipes().addAll(((SteamGrid) grid).getConnectedPipes());
        this.getTank().setCapacity(this.getCapacity());
        if (((SteamGrid) grid).getTank().getSteam() != 0)
            this.getTank().fillInternal(((SteamGrid) grid).getTank().getSteam(), true);
    }

    @Override
    void onSplit(final CableGrid grid)
    {
        this.getConnectedPipes().addAll(((SteamGrid) grid).getConnectedPipes().stream()
                .filter(this.getCables()::contains).collect(Collectors.toSet()));
        this.getTank()
                .fillInternal(((SteamGrid) grid).getTank().drainInternal(
                        ((SteamGrid) grid).getTank().getSteam() / grid.getCables().size() * this.getCables().size(),
                        false), true);
    }

    @Override
    void tick()
    {
        super.tick();

        final Set<ISteamHandler> handlers = this.connectedPipes.stream()
                .flatMap(pipe -> pipe.getConnectedHandlers().stream()).collect(Collectors.toSet());
        handlers.add(this.tank);

        final double average = handlers.stream().mapToDouble(handler -> handler.getPressure()).average().orElse(0);

        final ISteamHandler[] above = handlers.stream().filter(handler -> handler.getPressure() - average > 0)
                .toArray(ISteamHandler[]::new);
        final ISteamHandler[] below = handlers.stream().filter(handler -> handler.getPressure() - average < 0)
                .toArray(ISteamHandler[]::new);

        final int drained = Stream.of(above).mapToInt(handler ->
        {
            return handler.drainSteam(
                    Math.min((int) ((handler.getPressure() - average) * handler.getCapacity()), this.transferCapacity),
                    false);
        }).sum();
        int filled = 0;

        for (final ISteamHandler handler : below)
            filled += handler.fillSteam(
                    Math.max(drained / below.length, Math.min(
                            (int) ((handler.getPressure() - average) * handler.getCapacity()), this.transferCapacity)),
                    true);

        for (final ISteamHandler handler : above)
            handler.drainSteam(
                    Math.max(filled / above.length, Math.min(
                            (int) ((handler.getPressure() - average) * handler.getCapacity()), this.transferCapacity)),
                    true);
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

    public Set<ISteamPipe> getConnectedPipes()
    {
        return this.connectedPipes;
    }
}
