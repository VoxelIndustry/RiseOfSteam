package net.ros.common.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class RestrictedTank implements IFluidHandler
{
    private final IFluidHandler internalTank;
    private final boolean       canOutput, canInput;

    public RestrictedTank(IFluidHandler internalTank, boolean canOutput, boolean canInput)
    {
        this.internalTank = internalTank;
        this.canOutput = canOutput;
        this.canInput = canInput;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return this.internalTank.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (this.canInput)
            return this.internalTank.fill(resource, doFill);
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (this.canOutput)
            return this.internalTank.drain(resource, doDrain);
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (this.canOutput)
            return this.internalTank.drain(maxDrain, doDrain);
        return null;
    }
}