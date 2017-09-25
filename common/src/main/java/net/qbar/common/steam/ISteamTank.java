package net.qbar.common.steam;

import net.minecraftforge.fluids.FluidStack;

public interface ISteamTank extends ISteamHandler
{
    FluidStack toFluidStack();
}
