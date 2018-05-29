package net.ros.common.steam;

import lombok.Getter;
import net.minecraftforge.fluids.IFluidTank;

@Getter
public class LinkedSteamTank extends SteamTank
{
    private IFluidTank fluidTank;

    public LinkedSteamTank(int capacity, float maxPressure, IFluidTank fluidTank)
    {
        super(capacity, maxPressure);

        this.fluidTank = fluidTank;
    }

    @Override
    public int getCapacity()
    {
        return fluidTank.getFluidAmount();
    }
}
