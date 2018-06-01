package net.ros.common.fluid;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class LimitedTank extends FilteredFluidTank
{
    @Getter
    @Setter
    private int transferRate;

    public LimitedTank(final int capacity, final int transferRate)
    {
        super(capacity);

        this.transferRate = transferRate;
    }

    @Override
    public int fill(final FluidStack resource, final boolean doFill)
    {
        if (resource != null)
        {
            FluidStack copy = resource.copy();
            copy.amount = Math.min(copy.amount, this.transferRate);
            return super.fill(copy, doFill);
        }
        return super.fill(null, doFill);
    }

    @Override
    public FluidStack drain(final int maxDrain, final boolean doDrain)
    {
        return super.drain(Math.min(maxDrain, this.transferRate), doDrain);
    }

    public Fluid getFluidType()
    {
        if (this.getFluid() != null)
            return this.getFluid().getFluid();
        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("capacity", this.getCapacity());
        tag.setInteger("transferRate", this.transferRate);

        return super.writeToNBT(tag);
    }

    @Override
    public FluidTank readFromNBT(NBTTagCompound tag)
    {
        this.setCapacity(tag.getInteger("capacity"));
        this.setTransferRate(tag.getInteger("transferRate"));

        return super.readFromNBT(tag);
    }
}
