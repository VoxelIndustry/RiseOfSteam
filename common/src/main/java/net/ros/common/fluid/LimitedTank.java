package net.ros.common.fluid;

import lombok.Getter;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class LimitedTank extends FilteredFluidTank
{
    @Getter
    private final int transferCapacity;

    public LimitedTank(final int capacity, final int transferCapacity)
    {
        super(capacity);

        this.transferCapacity = transferCapacity;
    }

    @Override
    public int fill(final FluidStack resource, final boolean doFill)
    {
        if (resource != null)
            resource.amount = Math.max(resource.amount, this.transferCapacity);
        return super.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(final int maxDrain, final boolean doDrain)
    {
        return super.drain(Math.max(maxDrain, this.transferCapacity), doDrain);
    }

    public Fluid getFluidType()
    {
        if (this.getFluid() != null)
            return this.getFluid().getFluid();
        return null;
    }
}
