package net.qbar.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.common.fluid.DirectionalTank;

public class TileTank extends QBarTileBase
{
    private final DirectionalTank tank;

    public TileTank()
    {
        this.tank = new DirectionalTank("TileTank", new FluidTank(Fluid.BUCKET_VOLUME * 16),
                new EnumFacing[] { EnumFacing.DOWN }, new EnumFacing[] { EnumFacing.UP });
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        this.tank.writeToNBT(tag);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.tank.readFromNBT(tag);

        super.readFromNBT(tag);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return this.tank.canInteract(facing);
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) this.tank.getFluidHandler(facing);
        return super.getCapability(capability, facing);
    }
}