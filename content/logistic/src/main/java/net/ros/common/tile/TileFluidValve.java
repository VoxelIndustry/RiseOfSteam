package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.ros.common.fluid.LimitedTank;
import net.ros.common.grid.node.IPipeValve;
import net.ros.common.grid.node.PipeType;

import javax.annotation.Nullable;

public class TileFluidValve extends TileFluidPipe implements IPipeValve
{
    @Getter
    private boolean isOpen;

    public TileFluidValve(PipeType type)
    {
        super(type);
    }

    public TileFluidValve()
    {
        this(null);
    }

    @Override
    protected FluidTank createFluidTank(int capacity, int transferRate)
    {
        return new LimitedTank(capacity, transferRate)
        {
            private TileFluidValve valve;

            {
                this.valve = TileFluidValve.this;
            }

            @Override
            public int fill(FluidStack resource, boolean doFill)
            {
                if (valve.isOpen())
                    return super.fill(resource, doFill);
                return 0;
            }

            @Override
            public FluidStack drain(int maxDrain, boolean doDrain)
            {
                if (valve.isOpen())
                    return super.drain(maxDrain, doDrain);
                return null;
            }
        };
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.isOpen = tag.getBoolean("isOpen");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setBoolean("isOpen", this.isOpen);

        return super.writeToNBT(tag);
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        list.addText("Facing: " + this.getFacing());
        list.addText("Open: " + this.isOpen());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == this.capability)
            return facing != this.getFacing();
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == this.capability && facing != this.getFacing())
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getBufferTank());
        return super.getCapability(capability, facing);
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockDirectional.FACING);
    }

    public void setOpen(boolean isOpen)
    {
        this.isOpen = isOpen;
        this.sync();
    }
}
