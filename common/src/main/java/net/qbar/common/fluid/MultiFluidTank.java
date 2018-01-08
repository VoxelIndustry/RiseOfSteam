package net.qbar.common.fluid;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
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
    private int              tankCount;
    private List<FluidStack> fluids;

    public MultiFluidTank(int capacity, int tankCount)
    {
        this.capacity = capacity;
        this.tankCount = tankCount;
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
        int filled = 0;
        for (FluidStack fluid : fluids)
        {
            if (fluid.equals(resource))
            {
                filled += Math.min(this.capacity - fluid.amount, resource.amount - filled);
                if (doFill)
                    fluid.amount += filled;
                break;
            }
        }

        if (filled < resource.amount && this.fluids.size() < this.tankCount)
        {
            int leftover = Math.min(resource.amount - filled, this.capacity);
            filled += leftover;
            if (doFill)
                this.fluids.add(new FluidStack(resource, leftover));
        }
        return filled;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        int drained = 0;
        FluidStack toRemove = null;
        for (FluidStack fluid : fluids)
        {
            if (fluid.equals(resource))
            {
                drained = Math.min(resource.amount, fluid.amount);
                if (doDrain)
                {
                    fluid.amount -= drained;
                    if (fluid.amount <= 0)
                        toRemove = fluid;
                }
            }
        }
        if (toRemove != null)
            this.getFluids().remove(toRemove);
        if (drained != 0)
            return new FluidStack(resource, drained);
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return null;
    }

    public MultiFluidTank readFromNBT(NBTTagCompound nbt)
    {
        this.fluids.clear();
        if (!nbt.hasKey("Empty"))
        {
            for (int i = 0; i < nbt.getInteger("tankCount"); i++)
                this.fluids.add(FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid" + i)));
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (!this.fluids.isEmpty())
        {
            int i = 0;
            for (FluidStack fluid : this.fluids)
            {
                nbt.setTag("fluid" + i, fluid.writeToNBT(new NBTTagCompound()));
                i++;
            }
            nbt.setInteger("tankCount", i);
        }
        else
            nbt.setString("Empty", "");
        return nbt;
    }
}
