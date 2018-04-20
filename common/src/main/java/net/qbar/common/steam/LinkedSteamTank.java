package net.qbar.common.steam;

import lombok.Getter;
import net.minecraftforge.fluids.IFluidTank;

@Getter
public class LinkedSteamTank extends SteamTank
{
    private IFluidTank fluidTank;

    public LinkedSteamTank(int steamAmount, int capacity, float maxPressure, IFluidTank fluidTank)
    {
        super(steamAmount, capacity, maxPressure);

        this.fluidTank = fluidTank;
    }

    @Override
    public int getCapacity()
    {
        return fluidTank.getFluidAmount();
    }
}
