package net.ros.common.steam;

public class CreativeSteamTank extends SteamTank
{
    public CreativeSteamTank()
    {
        super(100_000, 1);
        this.setSteam(100_000);
    }

    @Override
    public int drainSteam(final int amount, final boolean doDrain)
    {
        return amount;
    }

    @Override
    public int fillSteam(final int amount, final boolean doFill)
    {
        return 0;
    }
}
