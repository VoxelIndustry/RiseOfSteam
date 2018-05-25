package net.ros.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.grid.impl.PipeGrid;
import net.ros.common.grid.node.IFluidPipe;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;

import java.util.List;

public class TileFluidPipe extends TilePipeBase<PipeGrid, IFluidHandler> implements IFluidPipe
{
    private FluidStack coldStorage;

    public TileFluidPipe(PipeType type, int transferCapacity)
    {
        super(type, transferCapacity, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

    public TileFluidPipe()
    {
        this(null, 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == this.capability)
            return (T) this.getGridObject().getTank();
        return super.getCapability(capability, facing);
    }

    @Override
    public void addSpecificInfo(final List<String> lines)
    {
        lines.add("Contains: " + (this.getGridObject().getTank().getFluidType() == null ? "none"
                : this.getGridObject().getTank().getFluidType().getName()));
        lines.add("Buffer: " + this.getGridObject().getTank().getFluidAmount() + " / "
                + this.getGridObject().getTank().getCapacity() + " mb");
    }

    @Override
    public void setGrid(final int gridIdentifier)
    {
        final int previous = this.grid;
        this.grid = gridIdentifier;

        if (gridIdentifier == -1)
            this.coldStorage = null;
        else if (this.coldStorage != null && previous == -1 && (this.getGridObject().isEmpty()
                || this.getGridObject().getFluid().equals(this.coldStorage.getFluid())))
        {
            this.getGridObject().getTank().fillInternal(this.coldStorage, true);
            this.coldStorage = null;
        }
        if (this.getGridObject() != null && !this.adjacentHandler.isEmpty())
            this.getGridObject().addOutput(this);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("coldStorage"))
            this.coldStorage = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("coldStorage"));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);

        this.toColdStorage();
        if (this.coldStorage != null)
        {
            final NBTTagCompound tag = new NBTTagCompound();
            this.coldStorage.writeToNBT(tag);
            tagCompound.setTag("coldStorage", tag);
        }
        return tagCompound;
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        if (to instanceof TileFluidPipe)
        {
            final PipeGrid grid = ((TileFluidPipe) to).getGridObject();
            if (grid != null)
            {
                if (this.coldStorage != null)
                {
                    return (grid.getFluid() == null || grid.getFluid().equals(this.coldStorage.getFluid())) &&
                            super.canConnect(facing, to);
                }
            }
            return super.canConnect(facing, to);
        }
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
                    this.getGridObject().removeOutput(this);
            }
            else if (tile.hasCapability(this.capability, facing) && !tile
                    .getCapability(this.capability, facing).equals(this.adjacentHandler.get(facing.getOpposite())))
            {
                this.connectHandler(facing.getOpposite(), tile.getCapability(this.capability, facing), tile);
                this.getGridObject().addOutput(this);
            }
        }
        else
        {
            if (tile != null)
            {
                if (tile.hasCapability(this.capability, facing) && !(tile instanceof TileFluidPipe))
                {
                    this.connectHandler(facing.getOpposite(), tile.getCapability(this.capability, facing), tile);
                    this.getGridObject().addOutput(this);
                }
            }
        }
    }

    @Override
    protected boolean keepAsValve(EnumFacing facing, TileEntity tile)
    {
        if (tile == null)
            return false;
        if (tile instanceof TileFluidValve && !((TileFluidValve) tile).isOpen() &&
                !((TileFluidValve) tile).isConnectionForbidden(facing.getOpposite()))
            return ((TileFluidValve) tile).getFacing().getOpposite() != facing;
        return false;
    }

    @Override
    public void fillNeighbors()
    {
        for (final IFluidHandler fluidHandler : this.adjacentHandler.values())
        {
            if (this.getGridObject().getTank().getFluidAmount() != 0 && fluidHandler != null)
            {
                final int simulated = fluidHandler
                        .fill(this.getGridObject().getTank().drain(this.getGridObject().getCapacity(), false), false);
                if (simulated > 0)
                    fluidHandler.fill(this.getGridObject().getTank().drain(simulated, true), true);
            }
        }
    }

    public void toColdStorage()
    {
        if (this.getGridObject() != null && this.getGridObject().getTank().getFluid() != null)
        {
            this.coldStorage = this.getGridObject().getTank().getFluid().copy();
            this.coldStorage.amount = this.coldStorage.amount / this.getGridObject().getCables().size();
        }
    }

    @Override
    public PipeGrid createGrid(final int id)
    {
        return new PipeGrid(id, this.transferCapacity);
    }
}
