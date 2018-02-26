package net.qbar.common.grid.impl;

import lombok.Getter;
import net.qbar.common.steam.ISteamHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SteamMesh implements ISteamHandler
{
    private List<ISteamHandler> handlers;
    @Getter
    private int                 transferCapacity;

    private double averagePressure;

    public SteamMesh(int transferCapacity)
    {
        this.handlers = new ArrayList<>();

        this.transferCapacity = transferCapacity;
    }

    public SteamMesh()
    {
        this(Integer.MAX_VALUE);
    }

    public void tick()
    {
        this.averagePressure = handlers.stream().mapToDouble(ISteamHandler::getPressure).average().orElse(0);

        if (handlers.size() <= 1)
            return;

        final ISteamHandler[] above = handlers.stream().filter(handler -> handler.getPressure() - averagePressure > 0)
                .toArray(ISteamHandler[]::new);
        final ISteamHandler[] below = handlers.stream().filter(handler -> handler.getPressure() - averagePressure < 0
                && handler.getPressure() < handler.getMaxPressure())
                .toArray(ISteamHandler[]::new);

        if (above.length == 0 || below.length == 0)
            return;

        final int drained = Stream.of(above).mapToInt(handler ->
                handler.drainSteam(
                        Math.min((int) ((handler.getPressure() - averagePressure) * handler.getCapacity()), this
                                .transferCapacity),
                        false)).sum();

        int filled = 0;


        for (final ISteamHandler handler : below)
            filled += handler.fillSteam(
                    Math.max(drained / below.length, Math.min(
                            (int) ((handler.getPressure() - averagePressure) * handler.getCapacity()), this
                                    .transferCapacity)),
                    true);

        for (final ISteamHandler handler : above)
            handler.drainSteam(
                    Math.max(filled / above.length, Math.min(
                            (int) ((handler.getPressure() - averagePressure) * handler.getCapacity()), this
                                    .transferCapacity)),
                    true);
    }

    public boolean addHandler(ISteamHandler handler)
    {
        if (this.handlers.contains(handler))
            return false;
        this.handlers.add(handler);
        return true;
    }

    public boolean removeHandler(ISteamHandler handler)
    {
        return this.handlers.remove(handler);
    }

    public boolean containsHandler(ISteamHandler handler)
    {
        return this.handlers.contains(handler);
    }


    ////////////////
    // STEAM TANK //
    ////////////////

    @Override
    public int drainSteam(int amount, boolean doDrain)
    {
        int toDrain = amount;
        int totalDrained = 0;

        int previousTotalSteam = this.getSteam();
        for (ISteamHandler handler : this.handlers)
        {
            int drained = handler.drainSteam(
                    (int) Math.min(toDrain, Math.ceil((double) amount * handler.getSteam() / previousTotalSteam)),
                    doDrain);
            totalDrained += drained;
            toDrain -= drained;

            if (toDrain <= 0)
                break;
        }
        return totalDrained;
    }

    @Override
    public int fillSteam(int amount, boolean doFill)
    {
        int toFill = amount;
        int totalFilled = 0;

        int previousFreeSteam = this.getFreeSpace();
        for (ISteamHandler handler : this.handlers)
        {
            int filled = handler.fillSteam(
                    (int) Math.min(toFill, Math.ceil((double) amount * handler.getFreeSpace() / previousFreeSteam)),
                    doFill);
            totalFilled += filled;
            toFill -= filled;

            if (toFill <= 0)
                break;
        }
        return totalFilled;
    }

    @Override
    public boolean canFill()
    {
        return true;
    }

    @Override
    public boolean canDrain()
    {
        return true;
    }

    @Override
    public float getPressure()
    {
        int pressure = 0;
        for (ISteamHandler handler : this.handlers)
            pressure += handler.getPressure();
        return pressure / handlers.size();
    }

    @Override
    public float getMaxPressure()
    {
        int maxPressure = 0;
        for (ISteamHandler handler : this.handlers)
            maxPressure += handler.getMaxPressure();
        return maxPressure / handlers.size();
    }

    @Override
    public int getSteam()
    {
        int steam = 0;
        for (ISteamHandler handler : this.handlers)
            steam += handler.getSteam();
        return steam;
    }

    @Override
    public int getCapacity()
    {
        int capacity = 0;
        for (ISteamHandler handler : this.handlers)
            capacity += handler.getCapacity();
        return capacity;
    }

    @Override
    public int getFreeSpace()
    {
        int freeSpace = 0;
        for (ISteamHandler handler : this.handlers)
            freeSpace += handler.getFreeSpace();
        return freeSpace;
    }
}
