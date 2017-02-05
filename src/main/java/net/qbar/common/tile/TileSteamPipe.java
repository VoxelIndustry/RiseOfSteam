package net.qbar.common.tile;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Optional;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.client.render.tile.VisibilityModelState;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.ISteamPipe;
import net.qbar.common.grid.ITileCable;
import net.qbar.common.grid.SteamGrid;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.steam.SteamUtil;

public class TileSteamPipe extends QBarTileBase implements ITileInfoProvider, ISteamPipe, ILoadable
{
    private int                                              grid;
    private final EnumMap<EnumFacing, ITileCable<SteamGrid>> connections;
    private final EnumMap<EnumFacing, ISteamHandler>         adjacentSteamHandler;

    private int                                              coldStorage;

    private int                                              transferCapacity;

    public TileSteamPipe(final int transferCapacity)
    {
        this.transferCapacity = transferCapacity;

        this.connections = new EnumMap<>(EnumFacing.class);
        this.adjacentSteamHandler = new EnumMap<>(EnumFacing.class);
        this.grid = -1;
    }

    public TileSteamPipe()
    {
        this(0);
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
            lines.add("Contains: " + this.getGridObject().getTank().getSteam() + " / "
                    + this.getGridObject().getTank().getCapacity());
            lines.add("Pressure " + SteamUtil.pressureFormat.format(this.getGridObject().getTank().getPressure())
                    + " / " + SteamUtil.pressureFormat.format(this.getGridObject().getTank().getMaxPressure()));
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
    public ITileCable<SteamGrid> getConnected(final EnumFacing facing)
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
            this.coldStorage = 0;
        else if (this.coldStorage != 0 && previous == -1)
        {
            this.getGridObject().getTank().fillInternal(this.coldStorage, true);
            this.coldStorage = 0;
        }
    }

    @Override
    public void readFromNBT(final NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);

        this.transferCapacity = tagCompound.getInteger("transferCapacity");
        this.coldStorage = tagCompound.getInteger("coldStorage");

        this.connections.clear();
        for (final EnumFacing facing : EnumFacing.VALUES)
        {
            if (tagCompound.hasKey("connected" + facing.ordinal()))
            {
                final Optional<ITileCable<SteamGrid>> connect = this.getWorldAdjacent(facing);

                if (connect.isPresent())
                    this.connect(facing, connect.get());
            }
            if (tagCompound.hasKey("connectedsteam" + facing.ordinal()))
            {
                final Optional<ISteamHandler> connect = this.getWorldAdjacentSteamHandler(facing);

                if (connect.isPresent())
                    this.connectSteamHandler(facing, connect.get());
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);

        tagCompound.setInteger("transferCapacity", this.transferCapacity);
        this.toColdStorage();
        if (this.coldStorage != 0)
            tagCompound.setInteger("coldStorage", this.coldStorage);
        for (final Entry<EnumFacing, ITileCable<SteamGrid>> entry : this.connections.entrySet())
            tagCompound.setBoolean("connected" + entry.getKey().ordinal(), true);
        for (final Entry<EnumFacing, ISteamHandler> entry : this.adjacentSteamHandler.entrySet())
            tagCompound.setBoolean("connectedsteam" + entry.getKey().ordinal(), true);
        return tagCompound;
    }

    public Optional<ITileCable<SteamGrid>> getWorldAdjacent(final EnumFacing facing)
    {
        final BlockPos search = this.pos.offset(facing);
        if (this.world != null && this.world.getTileEntity(search) != null
                && this.world.getTileEntity(search) instanceof ITileCable)
            return Optional.of((ITileCable<SteamGrid>) this.world.getTileEntity(search));
        return Optional.absent();
    }

    public Optional<ISteamHandler> getWorldAdjacentSteamHandler(final EnumFacing facing)
    {
        final BlockPos search = this.pos.offset(facing);
        if (this.world != null && this.world.getTileEntity(search) != null && this.world.getTileEntity(search)
                .hasCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, facing.getOpposite()))
            return Optional.of(this.world.getTileEntity(search)
                    .getCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, facing.getOpposite()));
        return Optional.absent();
    }

    @Override
    public void onChunkUnload()
    {
        GridManager.getInstance().disconnectCable(this);
    }

    @Override
    public boolean canConnect(final ITileCable<?> to)
    {
        if (to instanceof TileSteamPipe)
            return true;
        return false;
    }

    @Override
    public void connect(final EnumFacing facing, final ITileCable<SteamGrid> to)
    {
        this.connections.put(facing, to);
        this.updateState();
    }

    @Override
    public void disconnect(final EnumFacing facing)
    {
        this.connections.remove(facing);
        this.updateState();
    }

    public void connectSteamHandler(final EnumFacing facing, final ISteamHandler to)
    {
        this.adjacentSteamHandler.put(facing, to);
        this.updateState();
    }

    public void disconnectSteamHandler(final EnumFacing facing)
    {
        this.adjacentSteamHandler.remove(facing);
        this.updateState();
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
                this.disconnectSteamHandler(facing.getOpposite());
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
                    this.connectSteamHandler(facing.getOpposite(),
                            tile.getCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, facing));
                    this.getGridObject().addConnectedPipe(this);
                }
            }
        }
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (!this.world.isRemote && this.getGrid() == -1)
            TickHandler.loadables.add(this);
        else if (this.isClient())
            this.forceSync();
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
        if (this.getGridObject() != null && this.getGridObject().getTank().getSteam() != 0)
        {
            this.coldStorage = this.getGridObject().getTank().getSteam();
            this.coldStorage /= this.getGridObject().getCables().size();
        }
    }

    @Override
    public Collection<ISteamHandler> getConnectedHandlers()
    {
        return this.adjacentSteamHandler.values();
    }

    @Override
    public SteamGrid createGrid(final int id)
    {
        return new SteamGrid(id, this.transferCapacity);
    }

    ////////////
    // RENDER //
    ////////////

    public final VisibilityModelState state = new VisibilityModelState();

    private void updateState()
    {
        if (this.isServer())
        {
            this.sync();
            return;
        }
        this.state.hidden.clear();

        this.state.hidden.add("valvex1");
        this.state.hidden.add("valvex2");
        this.state.hidden.add("valvey1");
        this.state.hidden.add("valvey2");
        this.state.hidden.add("valvez1");
        this.state.hidden.add("valvez2");

        if (this.connections.isEmpty() && this.adjacentSteamHandler.isEmpty())
        {
            this.state.hidden.add("armx1");
            this.state.hidden.add("armx2");
            this.state.hidden.add("army1");
            this.state.hidden.add("army2");
            this.state.hidden.add("armz1");
            this.state.hidden.add("armz2");
            this.state.hidden.add("straightx");
            this.state.hidden.add("straighty");
            this.state.hidden.add("straightz");
        }
        else if (this.isStraight())
        {
            this.state.hidden.add("center");
            this.state.hidden.add("armx1");
            this.state.hidden.add("armx2");

            this.state.hidden.add("army1");
            this.state.hidden.add("army2");

            this.state.hidden.add("armz1");
            this.state.hidden.add("armz2");

            if (this.isConnected(EnumFacing.WEST))
            {
                this.state.hidden.add("straighty");
                this.state.hidden.add("straightz");
            }
            else if (this.isConnected(EnumFacing.NORTH))
            {
                this.state.hidden.add("straighty");
                this.state.hidden.add("straightx");
            }
            else if (this.isConnected(EnumFacing.UP))
            {
                this.state.hidden.add("straightx");
                this.state.hidden.add("straightz");
            }
        }
        else
        {
            this.state.hidden.add("straightx");
            this.state.hidden.add("straighty");
            this.state.hidden.add("straightz");

            if (!this.isConnected(EnumFacing.UP))
                this.state.hidden.add("army1");
            if (!this.isConnected(EnumFacing.DOWN))
                this.state.hidden.add("army2");
            if (!this.isConnected(EnumFacing.NORTH))
                this.state.hidden.add("armz1");
            if (!this.isConnected(EnumFacing.SOUTH))
                this.state.hidden.add("armz2");
            if (!this.isConnected(EnumFacing.EAST))
                this.state.hidden.add("armx1");
            if (!this.isConnected(EnumFacing.WEST))
                this.state.hidden.add("armx2");
        }

        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    private boolean isStraight()
    {
        if (this.connections.size() == 2)
            return this.isConnected(EnumFacing.NORTH) && this.isConnected(EnumFacing.SOUTH)
                    || this.isConnected(EnumFacing.WEST) && this.isConnected(EnumFacing.EAST)
                    || this.isConnected(EnumFacing.UP) && this.isConnected(EnumFacing.DOWN);
        return false;
    }

    private boolean isConnected(final EnumFacing facing)
    {
        return this.connections.containsKey(facing) || this.adjacentSteamHandler.containsKey(facing);
    }
}
