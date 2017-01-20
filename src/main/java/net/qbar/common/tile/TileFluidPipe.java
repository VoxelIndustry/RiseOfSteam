package net.qbar.common.tile;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.IFluidPipe;
import net.qbar.common.grid.ITileCable;
import net.qbar.common.grid.PipeGrid;

public class TileFluidPipe extends QBarTileBase implements ITileInfoProvider, IFluidPipe, ILoadable
{
    private int                                      grid;
    private final EnumMap<EnumFacing, ITileCable>    connections;
    private final EnumMap<EnumFacing, IFluidHandler> adjacentFluidHandler;

    private FluidStack                               coldStorage;

    private final int                                transferCapacity;

    public TileFluidPipe(final int transferCapacity)
    {
        this.transferCapacity = transferCapacity;

        this.connections = new EnumMap<>(EnumFacing.class);
        this.adjacentFluidHandler = new EnumMap<>(EnumFacing.class);
        this.grid = -1;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return this.getGrid() != -1 && this.getGridObject() != null;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) this.getGridObject().getTank();
        return super.getCapability(capability, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Grid: " + this.grid);

        if (this.getGrid() != -1 && this.getGridObject() != null)
        {
            lines.add("Contains: " + (this.getGridObject().getTank().getFluidType() == null ? "none"
                    : this.getGridObject().getTank().getFluidType().getName()));
            lines.add("Buffer: " + this.getGridObject().getTank().getFluidAmount() + " / "
                    + this.getGridObject().getTank().getCapacity() + " mb");
        }
        else
            lines.add("Errored grid!");
        this.connections.forEach((facing, cable) -> lines.add(facing + ": " + (cable != null)));
        this.adjacentFluidHandler.forEach((facing, handler) -> lines.add(facing + ": " + (handler != null)));
    }

    @Override
    public EnumFacing[] getConnections()
    {
        return this.connections.keySet().toArray(new EnumFacing[0]);
    }

    @Override
    public ITileCable getConnected(final EnumFacing facing)
    {
        return this.connections.get(facing);
    }

    @Override
    public int getGrid()
    {
        return this.grid;
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
    }

    @Override
    public void readFromNBT(final NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);

        if (tagCompound.hasKey("coldStorage"))
            this.coldStorage = FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag("coldStorage"));
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
    public void onChunkUnload()
    {
        GridManager.getInstance().disconnectCable(this);
    }

    @Override
    public boolean canConnect(final ITileCable to)
    {
        if (to instanceof TileFluidPipe)
        {
            final PipeGrid grid = ((TileFluidPipe) to).getGridObject();
            if (grid != null)
            {
                if (this.coldStorage != null)
                {
                    if (grid.getFluid() == null || grid.getFluid().equals(this.coldStorage.getFluid()))
                        return true;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void connect(final EnumFacing facing, final ITileCable to)
    {
        this.connections.put(facing, to);
    }

    @Override
    public void disconnect(final EnumFacing facing)
    {
        this.connections.remove(facing);
    }

    public void scanFluidHandlers(final BlockPos posNeighbor)
    {
        final TileEntity tile = this.world.getTileEntity(posNeighbor);

        final BlockPos substract = posNeighbor.subtract(this.pos);
        final EnumFacing facing = EnumFacing.getFacingFromVector(substract.getX(), substract.getY(), substract.getZ())
                .getOpposite();

        if (this.adjacentFluidHandler.containsKey(facing.getOpposite()))
        {
            if (tile == null || !tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing))
            {
                this.adjacentFluidHandler.remove(facing.getOpposite());
                if (this.adjacentFluidHandler.isEmpty())
                    this.getGridObject().removeOutput(this);
            }
        }
        else
        {
            if (tile != null)
            {
                if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)
                        && !(tile instanceof TileFluidPipe))
                {
                    this.adjacentFluidHandler.put(facing.getOpposite(),
                            tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing));
                    this.getGridObject().addOutput(this);
                }
            }
        }
    }

    @Override
    public void fillNeighbors()
    {
        for (final IFluidHandler fluidHandler : this.adjacentFluidHandler.values())
        {
            if (this.getGridObject().getTank().getFluidAmount() != 0)
            {
                final int simulated = fluidHandler
                        .fill(this.getGridObject().getTank().drain(this.getGridObject().getCapacity(), false), false);
                if (simulated > 0)
                    fluidHandler.fill(this.getGridObject().getTank().drain(simulated, true), true);
            }
        }
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (!this.world.isRemote && this.getGrid() == -1)
            TickHandler.loadables.add(this);
    }

    @Override
    public void load()
    {
        GridManager.getInstance().connectCable(this);
        for (final EnumFacing facing : EnumFacing.VALUES)
            this.scanFluidHandlers(this.pos.offset(facing));
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
