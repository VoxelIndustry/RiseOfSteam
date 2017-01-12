package net.qbar.common.fluid;

import java.util.function.Predicate;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FilteredFluidTank extends FluidTank
{
    private final Predicate<FluidStack> filter;

    public FilteredFluidTank(final int capacity, final Predicate<FluidStack> filter)
    {
        super(capacity);

        this.filter = filter;
    }

    @Override
    public boolean canDrainFluidType(final FluidStack fluid)
    {
        return this.filter.test(fluid);
    }

    @Override
    public boolean canFillFluidType(final FluidStack fluid)
    {
        return this.filter.test(fluid);
    }
}
