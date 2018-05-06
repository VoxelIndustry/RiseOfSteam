package net.ros.common.fluid;

import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.HashSet;

public class DirectionalTank
{
    private final HashSet<EnumFacing> outputs, inputs;
    private final FluidTank internalTank;
    private final String    name;

    private final RestrictedTank outputOnly, inputOnly, both;

    public DirectionalTank(final String name, final FluidTank tank, final EnumFacing[] outputs,
                           final EnumFacing[] inputs)
    {
        this.outputs = Sets.newHashSet(outputs);
        this.inputs = Sets.newHashSet(inputs);

        this.internalTank = tank;
        this.name = name;

        this.outputOnly = new RestrictedTank(this.internalTank, true, false);
        this.inputOnly = new RestrictedTank(this.internalTank, false, true);
        this.both = new RestrictedTank(this.internalTank, true, true);
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
        tag.setInteger("capacity", this.internalTank.getCapacity());
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
            this.internalTank.setCapacity(tag.getInteger("capacity"));
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

    public FluidTank getInternalFluidHandler()
    {
        return this.internalTank;
    }

    public void setFluidStack(final FluidStack fluid)
    {
        this.internalTank.setFluid(fluid);
    }

    public IFluidHandler getInputHandler()
    {
        return this.inputOnly;
    }

    public IFluidHandler getOutputHandler()
    {
        return this.outputOnly;
    }
}
