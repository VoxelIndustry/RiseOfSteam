package net.qbar.common.steam;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public interface ISteamTank extends ISteamHandler
{
    FluidStack toFluidStack();

    void readFromNBT(NBTTagCompound nbt);

    NBTTagCompound writeToNBT(NBTTagCompound nbt);

    void setSteam(int steam);
}
