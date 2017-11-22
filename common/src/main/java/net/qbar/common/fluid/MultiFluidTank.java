package net.qbar.common.fluid;

import lombok.Getter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MultiFluidTank implements IFluidTank, IFluidHandler
{
    private int              capacity;
    private List<FluidStack> fluids;

    public MultiFluidTank(int capacity)
    {
        this.capacity = capacity;
        this.fluids = new ArrayList<>();
    }

    @Nullable
    @Override
    public FluidStack getFluid()
    {
        return this.fluids.isEmpty() ? null : this.fluids.get(0);
    }

    @Override
    public int getFluidAmount()
    {
        int sum = 0;
        for (FluidStack fluid : this.fluids)
        {
            int amount = fluid.amount;
            sum += amount;
        }
        return sum;
    }

    @Override
    public FluidTankInfo getInfo()
    {
        FluidStack fluid = this.getFluid();
        int capacity2 = this.getCapacity() - this.getFluidAmount();
        if (fluid != null)
            capacity2 += fluid.amount;
        return new FluidTankInfo(fluid, capacity2);
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        if (this.fluids.isEmpty())
            return new IFluidTankProperties[0];
        IFluidTankProperties[] properties = new IFluidTankProperties[fluids.size()];
        for (int i = 0; i < fluids.size(); i++)
        {
            boolean first = i == 0;
            int capacity2 = fluids.get(i).amount;
            if (first)
                capacity2 += getCapacity() - getFluidAmount();
            properties[i] = new FluidTankProperties(fluids.get(i), capacity2, first, first);
        }

        return properties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return null;
    }
}
