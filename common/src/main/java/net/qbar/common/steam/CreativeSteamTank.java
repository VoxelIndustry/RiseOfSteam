package net.qbar.common.steam;

public class CreativeSteamTank extends SteamTank
{

    public CreativeSteamTank()
    {
        super(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
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