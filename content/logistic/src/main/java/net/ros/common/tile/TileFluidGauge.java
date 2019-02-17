package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

import javax.annotation.Nullable;

public class TileFluidGauge extends TileFluidPipe implements ITickable
{
    @Getter
    private FluidStack currentFluid;

    public TileFluidGauge(PipeType type)
    {
        super(type);
    }

    public TileFluidGauge()
    {
        this(null);
    }

    @Override
    public void update()
    {
        if (this.isClient() || !this.hasGrid())
            return;

        if (this.world.getTotalWorldTime() % 10 == 0)
        {
            FluidStack newFluid = this.getBufferTank().getFluid();

            if (currentFluid == null && newFluid != null || currentFluid != null && newFluid == null ||
                    currentFluid != null && !currentFluid.isFluidStackIdentical(newFluid))
            {
                if (newFluid != null)
                    this.currentFluid = newFluid.copy();
                else
                    this.currentFluid = null;
                this.sync();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        return super.writeToNBT(tag);
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        list.addText("Facing: " + this.getFacing());
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

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        if (facing == this.getFacing())
            return false;

        return super.canConnect(facing, to);
    }
}
