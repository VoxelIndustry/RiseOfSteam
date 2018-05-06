package net.ros.common.fluid;

import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.function.Predicate;

public class FilteredFluidTank extends FluidTank
{
    static final Predicate<FluidStack> ANY = fluid -> true;
    @Getter
    @Setter
    private      Predicate<FluidStack> filter;

    public FilteredFluidTank(final int capacity, final Predicate<FluidStack> filter)
    {
        super(capacity);

        this.filter = filter;
    }

    public FilteredFluidTank(int capacity)
    {
        this(capacity, ANY);
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
