package net.qbar.common.steam;

public interface ISteamHandler
{
    /**
     * @param amount  of steam to be drained from the handler.
     * @param doDrain
     * @return the SteamStack drained from the handler or what would have been
     * drained if simulation was activated.
     */
    int drainSteam(int amount, boolean doDrain);

    /**
     * @param steam  The SteamStack than must be filled inside the handler.
     * @param doFill
     * @return the SteamStack after a fill from the handler or what it would
     * have been if simulation was activated.
     */
    int fillSteam(int steam, boolean doFill);

    boolean canFill();

    boolean canDrain();

    float getPressure();

    float getMaxPressure();

    int getSteam();

    int getCapacity();

    default int getFreeSpace()
    {
        return (int) (this.getCapacity() * this.getMaxPressure() - this.getSteam());
    }
}
