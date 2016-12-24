package net.qbar.common.steam;

public interface ISteamHandler
{
    /**
     *
     * @param amount
     *            of steam to be drained from the handler.
     * @param simulated
     * @return the SteamStack drained from the handler or what would have been
     *         drained if simulation was activated.
     */
    SteamStack drainSteam(int amount, boolean simulated);

    /**
     *
     * @param steam
     *            The SteamStack than must be filled inside the handler.
     * @param simulated
     * @return the SteamStack after a fill from the handler or what it would
     *         have been if simulation was activated.
     */
    SteamStack fillSteam(SteamStack steam, boolean simulated);
}
