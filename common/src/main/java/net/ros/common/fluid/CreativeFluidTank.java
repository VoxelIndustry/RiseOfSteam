package net.ros.common.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;

public class CreativeFluidTank extends FluidTank
{
    public CreativeFluidTank(@Nullable Fluid fluid)
    {
        super(Integer.MAX_VALUE);
        this.fluid = new FluidStack(fluid, Integer.MAX_VALUE);
    }
 
    @Override
    public int getFluidAmount()
    {
        return Integer.MAX_VALUE;
    }
}
