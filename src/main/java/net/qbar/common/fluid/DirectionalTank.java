package net.qbar.common.fluid;

import java.util.HashSet;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class DirectionalTank
{
    private final HashSet<EnumFacing>    outputs, inputs;
    private final FluidTank              internalTank;
    private final String                 name;

    private final InternalRestrictedTank outputOnly, inputOnly, both;

    public DirectionalTank(final String name, final FluidTank tank, final EnumFacing[] outputs,
            final EnumFacing[] inputs)
    {
        this.outputs = Sets.newHashSet(outputs);
        this.inputs = Sets.newHashSet(inputs);

        this.internalTank = tank;
        this.name = name;

        this.outputOnly = new InternalRestrictedTank(this.internalTank, true, false);
        this.inputOnly = new InternalRestrictedTank(this.internalTank, false, true);
        this.both = new InternalRestrictedTank(this.internalTank, true, true);
    }

    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        final NBTTagCompound subTag = new NBTTagCompound();
        for (final EnumFacing facing : EnumFacing.VALUES)
        {
            if (this.outputs.contains(facing) && this.inputs.contains(facing))
                subTag.setString(facing.name(), "both");
            else if (this.outputs.contains(facing))
                subTag.setString(facing.name(), "output");
            else if (this.inputs.contains(facing))
                subTag.setString(facing.name(), "inputs");
        }
        this.internalTank.writeToNBT(subTag);
        tag.setTag(this.name, subTag);
        return tag;
    }

    public DirectionalTank readFromNBT(final NBTTagCompound tag)
    {
        if (tag.hasKey(this.name))
        {
            final NBTTagCompound subTag = tag.getCompoundTag(this.name);
            for (final EnumFacing facing : EnumFacing.VALUES)
            {
                final String type = subTag.getString(facing.name());
                if (type.equals("both"))
                {
                    this.outputs.add(facing);
                    this.inputs.add(facing);
                }
                else if (type.equals("output"))
                    this.outputs.add(facing);
                else if (type.equals("input"))
                    this.inputs.add(facing);
            }
            this.internalTank.readFromNBT(subTag);
        }
        return this;
    }

    public boolean canInteract(final EnumFacing facing)
    {
        return this.outputs.contains(facing) || this.inputs.contains(facing);
    }

    @Nullable
    public IFluidHandler getFluidHandler(final EnumFacing facing)
    {
        if (this.outputs.contains(facing) && this.inputs.contains(facing))
            return this.both;
        else if (this.outputs.contains(facing))
            return this.outputOnly;
        else if (this.inputs.contains(facing))
            return this.inputOnly;
        else
            return null;
    }

    private static class InternalRestrictedTank implements IFluidHandler
    {
        private final FluidTank internalTank;
        private final boolean   canOutput, canInput;

        private InternalRestrictedTank(final FluidTank internalTank, final boolean canOutput, final boolean canInput)
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
        public int fill(final FluidStack resource, final boolean doFill)
        {
            if (this.canInput)
                return this.internalTank.fill(resource, doFill);
            return 0;
        }

        @Override
        public FluidStack drain(final FluidStack resource, final boolean doDrain)
        {
            if (this.canOutput)
                return this.internalTank.drain(resource, doDrain);
            return null;
        }

        @Override
        public FluidStack drain(final int maxDrain, final boolean doDrain)
        {
            if (this.canOutput)
                return this.internalTank.drain(maxDrain, doDrain);
            return null;
        }
    }
}
