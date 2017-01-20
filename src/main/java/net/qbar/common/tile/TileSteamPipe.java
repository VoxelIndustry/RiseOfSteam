package net.qbar.common.tile;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.CableGrid;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.ISteamPipe;
import net.qbar.common.grid.ITileCable;
import net.qbar.common.grid.SteamGrid;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.steam.SteamStack;

public class TileSteamPipe extends QBarTileBase implements ITileInfoProvider, ISteamPipe, ILoadable
{
    private int                                      grid;
    private final EnumMap<EnumFacing, ITileCable>    connections;
    private final EnumMap<EnumFacing, ISteamHandler> adjacentSteamHandler;

    private SteamStack                               coldStorage;

    private final int                                transferCapacity;

    public TileSteamPipe(final int transferCapacity)
    {
        this.transferCapacity = transferCapacity;

        this.connections = new EnumMap<>(EnumFacing.class);
        this.adjacentSteamHandler = new EnumMap<>(EnumFacing.class);
        this.grid = -1;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return this.getGrid() != -1 && this.getGridObject() != null;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return (T) this.getGridObject().getTank();
        return super.getCapability(capability, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Grid: " + this.grid);

        if (this.getGrid() != -1 && this.getGridObject() != null)
        {
            lines.add("Contains: " + this.getGridObject().getTank().getAmount() + " / "
                    + this.getGridObject().getTank().getCapacity());
            lines.add("Pressure : " + this.getGridObject().getTank().getPressure() + " / "
                    + this.getGridObject().getTank().getMaxPressure());
        }
        else
            lines.add("Errored grid!");
        this.connections.forEach((facing, cable) -> lines.add(facing + ": " + (cable != null)));
        this.adjacentSteamHandler.forEach((facing, handler) -> lines.add(facing + ": " + (handler != null)));
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
        else if (this.coldStorage != null && previous == -1 && this.getGridObject().isEmpty())
        {
            this.getGridObject().getTank().fillInternal(this.coldStorage.getAmount(), true);
            this.coldStorage = null;
        }
    }

    @Override
    public void readFromNBT(final NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);

        if (tagCompound.hasKey("coldStorage"))
            this.coldStorage = SteamStack.readFromNBT(tagCompound.getCompoundTag("coldStorage"));
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
        if (to instanceof TileSteamPipe)
            return true;
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

    public void scanSteamHandlers(final BlockPos posNeighbor)
    {
        final TileEntity tile = this.world.getTileEntity(posNeighbor);

        final BlockPos substract = posNeighbor.subtract(this.pos);
        final EnumFacing facing = EnumFacing.getFacingFromVector(substract.getX(), substract.getY(), substract.getZ())
                .getOpposite();

        if (this.adjacentSteamHandler.containsKey(facing.getOpposite()))
        {
            if (tile == null || !tile.hasCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, facing))
            {
                this.adjacentSteamHandler.remove(facing.getOpposite());
                if (this.adjacentSteamHandler.isEmpty())
                    this.getGridObject().removeConnectedPipe(this);
            }
        }
        else
        {
            if (tile != null)
            {
                if (tile.hasCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, facing)
                        && !(tile instanceof TileSteamPipe))
                {
                    this.adjacentSteamHandler.put(facing.getOpposite(),
                            tile.getCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, facing));
                    this.getGridObject().addConnectedPipe(this);
                }
            }
        }
    }

    @Override
    public void supplyNeighbors()
    {
        for (final ISteamHandler steamHandler : this.adjacentSteamHandler.values())
        {
            if (this.getGridObject().getTank().getAmount() != 0)
            {
                // Supply and pressure repartition code
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
            this.scanSteamHandlers(this.pos.offset(facing));
    }

    public void toColdStorage()
    {
        if (this.getGridObject() != null && this.getGridObject().getTank().getAmount() != 0)
        {
            this.coldStorage = this.getGridObject().getTank().getSteam().copy();
            this.coldStorage.setAmount(this.coldStorage.getAmount() / this.getGridObject().getCables().size());
        }
    }

    @Override
    public Collection<ISteamHandler> getConnectedHandlers()
    {
        return this.adjacentSteamHandler.values();
    }

    @Override
    public CableGrid createGrid(final int id)
    {
        return new SteamGrid(id, this.transferCapacity);
    }
}
