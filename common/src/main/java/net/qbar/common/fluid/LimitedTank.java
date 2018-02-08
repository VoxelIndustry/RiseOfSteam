package net.qbar.common.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class LimitedTank extends FluidTank
{
    private final String name;
    private final int    transferCapacity;

    public LimitedTank(final String name, final int capacity, final int transferCapacity)
    {
        super(capacity);

        this.name = name;
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
