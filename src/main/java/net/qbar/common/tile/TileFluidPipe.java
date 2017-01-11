package net.qbar.common.tile;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.CableGrid;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.IFluidPipe;
import net.qbar.common.grid.ITileCable;
import net.qbar.common.grid.PipeGrid;

public class TileFluidPipe extends QBarTileBase implements ITileInfoProvider, IFluidPipe, ILoadable
{
    private int                                      grid;
    private final EnumMap<EnumFacing, ITileCable>    connections;
    private final EnumMap<EnumFacing, IFluidHandler> adjacentFluidHandler;

    public TileFluidPipe()
    {
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

    public PipeGrid getGridObject()
    {
        final CableGrid grid = GridManager.getInstance().getGrid(this.getGrid());

        if (grid != null && grid instanceof PipeGrid)
            return (PipeGrid) grid;
        return null;
    }

    @Override
    public void setGrid(final int gridIdentifier)
    {
        this.grid = gridIdentifier;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public void onChunkUnload()
    {
        GridManager.getInstance().disconnectCable(this);
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
}
