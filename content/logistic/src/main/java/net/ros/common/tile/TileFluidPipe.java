package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.fluid.LimitedTank;
import net.ros.common.grid.impl.PipeGrid;
import net.ros.common.grid.node.IFluidPipe;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;

import java.util.List;

public class TileFluidPipe extends TilePipeBase<PipeGrid, IFluidHandler> implements IFluidPipe
{
    @Getter
    private FluidTank bufferTank;

    public TileFluidPipe(PipeType type, int transferCapacity)
    {
        super(type, transferCapacity, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);

        this.bufferTank = this.createFluidTank(transferCapacity * 4, this.getTransferRate());
    }

    public TileFluidPipe()
    {
        this(null, 0);
    }

    protected FluidTank createFluidTank(int capacity, int transferRate)
    {
        return new LimitedTank(capacity, transferRate);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == this.capability)
            return (T) this.getBufferTank();
        return super.getCapability(capability, facing);
    }

    @Override
    public void addSpecificInfo(final List<String> lines)
    {
        lines.add("Contains: " + (this.getBufferTank().getFluid() == null ? "none"
                : this.getBufferTank().getFluid().getFluid().getName()));
        lines.add("Buffer: " + this.getBufferTank().getFluidAmount() + " / "
                + this.getBufferTank().getCapacity() + " mb");
    }

    @Override
    public void setGrid(final int gridIdentifier)
    {
        this.grid = gridIdentifier;

        if (this.getGridObject() != null && !this.adjacentHandler.isEmpty())
            this.getGridObject().addConnectedPipe(this);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("bufferTank"))
            this.bufferTank.readFromNBT(tag.getCompoundTag("bufferTank"));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setTag("bufferTank", this.bufferTank.writeToNBT(new NBTTagCompound()));

        return tag;
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        if (to instanceof TileFluidPipe)
            return super.canConnect(facing, to);
        return false;
    }

    @Override
    public void scanHandler(final BlockPos posNeighbor)
    {
        final TileEntity tile = this.world.getTileEntity(posNeighbor);

        final BlockPos substract = posNeighbor.subtract(this.pos);
        final EnumFacing facing = EnumFacing.getFacingFromVector(substract.getX(), substract.getY(), substract.getZ())
                .getOpposite();

        if (this.adjacentHandler.containsKey(facing.getOpposite()))
        {
            if (tile == null || !tile.hasCapability(this.capability, facing))
            {
                this.disconnectHandler(facing.getOpposite(), tile);
                if (this.adjacentHandler.isEmpty())
                    this.getGridObject().removeConnectedPipe(this);
            }
            else if (tile.hasCapability(this.capability, facing) && !tile
                    .getCapability(this.capability, facing).equals(this.adjacentHandler.get(facing.getOpposite())))
            {
                this.connectHandler(facing.getOpposite(), tile.getCapability(this.capability, facing), tile);
                this.getGridObject().addConnectedPipe(this);
            }
        }
        else
        {
            if (tile != null)
            {
                if (tile.hasCapability(this.capability, facing) && !(tile instanceof TileFluidPipe))
                {
                    this.connectHandler(facing.getOpposite(), tile.getCapability(this.capability, facing), tile);
                    this.getGridObject().addConnectedPipe(this);
                }
            }
        }
    }

    @Override
    public void fillNeighbors()
    {
        for (IFluidHandler fluidHandler : this.adjacentHandler.values())
        {
            if (this.getBufferTank().getFluidAmount() != 0 && fluidHandler != null)
            {
                int simulated = fluidHandler.fill(this.getBufferTank().drain(this.getTransferRate(), false), false);

                if (simulated > 0)
                    fluidHandler.fill(this.getBufferTank().drain(simulated, true), true);
            }
        }
    }

    @Override
    public boolean isInput()
    {
        return false;
    }

    @Override
    public boolean isOutput()
    {
        return true;
    }

    @Override
    public PipeGrid createGrid(final int id)
    {
        return new PipeGrid(id);
    }
}
