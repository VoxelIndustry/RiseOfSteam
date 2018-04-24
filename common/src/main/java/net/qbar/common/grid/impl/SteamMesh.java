package net.qbar.common.grid.impl;

import lombok.Getter;
import net.qbar.common.QBarConstants;
import net.qbar.common.steam.ISteamHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SteamMesh implements ISteamHandler
{
    @Getter
    private List<ISteamHandler> handlers;
    @Getter
    private int                 throttle;

    private double averagePressure;

    public SteamMesh(int throttle)
    {
        this.handlers = new ArrayList<>();

        this.throttle = throttle;
    }

    public SteamMesh()
    {
        this(Integer.MAX_VALUE);
    }

    public void tick()
    {
        this.averagePressure = this.getPressure();

        if (handlers.size() <= 1)
            return;

        /*
         * All handlers above and below the average pressure are gathered.
         * To prevent useless repartition the actual steam difference must be greater than 16.
         * In the below case, a check is performed to filter full handlers that will explode anyway.
         */
        final ISteamHandler[] above = handlers.stream().filter(handler -> handler.getPressure() - averagePressure > 0
                && handler.getSteamDifference((float) averagePressure) > 16)
                .toArray(ISteamHandler[]::new);
        final ISteamHandler[] below = handlers.stream().filter(handler -> handler.getPressure() - averagePressure < 0
                && handler.getSteamDifference((float) averagePressure) < -16
                && handler.getPressure() < handler.getMaxPressure())
                .toArray(ISteamHandler[]::new);

        if (above.length == 0 || below.length == 0)
            return;

        // Fake drain to check for handlers throttling and change the simulation accordingly
        final int drained = Stream.of(above).mapToInt(handler ->
                handler.drainSteam(
                        Math.min((int) Math.ceil((handler.getPressure() - averagePressure) * handler.getCapacity()),
                                this.throttle),
                        false)).sum();

        int filled = 0;

        // Real fill to also check for throttling and make sure we never lose steam
        for (final ISteamHandler handler : below)
            filled += handler.fillSteam(
                    Math.max(drained / below.length, Math.min(
                            (int) Math.ceil((handler.getPressure() - averagePressure) * handler.getCapacity()),
                            this.throttle)),
                    true);

        // Real drain once we know how much can be extracted and how much can me inserted
        for (final ISteamHandler handler : above)
            handler.drainSteam(
                    Math.min(filled / above.length, Math.min(
                            (int) Math.ceil((handler.getPressure() - averagePressure) * handler.getCapacity()),
                            this.throttle)),
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
        return this.internalDrain(Math.min(amount, this.throttle), doDrain);
    }

    private int internalDrain(int amount, boolean doDrain)
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
        return this.internalFill(Math.min(amount, this.throttle), doFill);
    }

    private int internalFill(int amount, boolean doFill)
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
        return (float) this.getSteam() / this.getCapacity();
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

        if (capacity < 0)
            QBarConstants.LOGGER.warn("SteamMesh capacity compute returned a negative value!" +
                    " Something very wrong happened with your grid.");
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
