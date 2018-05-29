package net.ros.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.client.render.model.obj.PipeOBJStates;
import net.ros.client.render.model.obj.ROSOBJState;
import net.ros.common.event.TickHandler;
import net.ros.common.grid.GridManager;
import net.ros.common.grid.IConnectionAware;
import net.ros.common.grid.impl.CableGrid;
import net.ros.common.grid.node.IPipe;
import net.ros.common.grid.node.ITileCable;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;
import net.ros.common.network.PipeUpdatePacket;

import java.util.*;
import java.util.Map.Entry;

public abstract class TilePipeBase<G extends CableGrid, H> extends TileBase
        implements ILoadable, IPipe<G>
{
    protected final EnumSet<EnumFacing>                renderConnections;
    protected final EnumSet<EnumFacing>                forbiddenConnections;
    protected       boolean                            forbiddenSwitch;
    @Getter
    protected final EnumMap<EnumFacing, ITileCable<G>> connectionsMap;
    protected final EnumMap<EnumFacing, H>             adjacentHandler;
    protected final Capability<H>                      capability;
    @Getter
    @Setter
    protected       int                                grid;
    @Getter
    private         int                                transferCapacity;

    @Getter
    private PipeType type;

    public TilePipeBase(PipeType type, int transferCapacity, Capability<H> capability)
    {
        this.type = type;
        this.transferCapacity = transferCapacity;
        this.capability = capability;

        this.connectionsMap = new EnumMap<>(EnumFacing.class);
        this.adjacentHandler = new EnumMap<>(EnumFacing.class);
        this.grid = -1;

        this.renderConnections = EnumSet.noneOf(EnumFacing.class);
        this.forbiddenConnections = EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == this.capability)
            return this.getGrid() != -1 && this.getGridObject() != null && !this.forbiddenConnections.contains(facing);
        return super.hasCapability(capability, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Grid: " + this.grid);

        if (this.getGrid() != -1 && this.getGridObject() != null)
            this.addSpecificInfo(lines);
        else
            lines.add("Errored grid!");
        this.connectionsMap.forEach((facing, cable) -> lines.add("Pipe " + facing + ": " + (cable != null)));
        this.adjacentHandler.forEach((facing, handler) -> lines.add("Handler " + facing + ": " + (handler != null)));
    }

    public void addSpecificInfo(final List<String> lines)
    {
    }

    public Collection<H> getConnectedHandlers()
    {
        return this.adjacentHandler.values();
    }

    @Override
    public void connect(EnumFacing facing, ITileCable<G> to)
    {
        this.getConnectionsMap().put(facing, to);
    }

    @Override
    public void disconnect(final EnumFacing facing)
    {
        this.connectionsMap.remove(facing);
        this.updateState();
    }

    public void connectHandler(final EnumFacing facing, final H to, final TileEntity tile)
    {
        this.adjacentHandler.put(facing, to);
        this.updateState();

        if (tile instanceof IConnectionAware)
            ((IConnectionAware) tile).connectTrigger(facing.getOpposite(), this.getGridObject());
    }

    public void disconnectHandler(final EnumFacing facing, final TileEntity tile)
    {
        this.adjacentHandler.remove(facing);
        this.updateState();

        if (tile instanceof IConnectionAware)
            ((IConnectionAware) tile).disconnectTrigger(facing.getOpposite(), this.getGridObject());
    }

    public void disconnectItself()
    {
        GridManager.getInstance().disconnectCable(this);

        this.adjacentHandler.keySet().forEach(facing ->
        {
            TileEntity handler = this.getBlockWorld().getTileEntity(this.getBlockPos().offset(facing));
            if (handler instanceof IConnectionAware)
                ((IConnectionAware) handler).disconnectTrigger(facing.getOpposite(), this.getGridObject());
        });
    }

    @Override
    public void onChunkUnload()
    {
        this.disconnectItself();
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (!this.world.isRemote && this.getGrid() == -1)
            TickHandler.loadables.add(this);
        else if (this.isClient())
        {
            this.askServerSync();
        }
    }

    @Override
    public void load()
    {
        GridManager.getInstance().connectCable(this);
        for (EnumFacing facing : EnumFacing.VALUES)
            this.scanHandler(facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.transferCapacity = tag.getInteger("transferCapacity");
        this.type = new PipeType(tag.getCompoundTag("type"));

        if (this.isClient())
        {
            if (this.readRenderConnections(tag))
                this.updateState();
        }

        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (tag.getBoolean("forbid" + facing.ordinal()))
                this.forbiddenConnections.add(facing);
        }
        this.forbiddenSwitch = tag.getBoolean("forbiddenSwitch");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("transferCapacity", this.transferCapacity);
        tag.setTag("type", this.getType().toNBT(new NBTTagCompound()));

        if (this.isServer())
            this.writeRenderConnections(tag);

        for (EnumFacing facing : EnumFacing.VALUES)
            tag.setBoolean("forbid" + facing.ordinal(), this.forbiddenConnections.contains(facing));
        tag.setBoolean("forbiddenSwitch", this.forbiddenSwitch);

        return tag;
    }

    public void scanHandler(EnumFacing facing)
    {
        if (!this.forbiddenConnections.contains(facing))
            this.scanHandler(this.getPos().offset(facing));
    }

    protected abstract void scanHandler(BlockPos posNeighbor);

    @Override
    public void adjacentConnect()
    {
        List<TilePipeBase> adjacents = new ArrayList<>(6);
        for (final EnumFacing facing : EnumFacing.VALUES)
        {
            final TileEntity adjacent = this.getBlockWorld().getTileEntity(this.getAdjacentPos(facing));
            if (adjacent instanceof TilePipeBase && this.canConnect(facing, (ITileCable<?>) adjacent)
                    && ((ITileCable<?>) adjacent).canConnect(facing.getOpposite(), this))
            {
                this.connect(facing, (TilePipeBase) adjacent);
                ((TilePipeBase) adjacent).connect(facing.getOpposite(), this);
                adjacents.add((TilePipeBase) adjacent);
            }
        }
        new PipeUpdatePacket(this, adjacents).sendToAllIn(this.getWorld());
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        return !this.forbiddenConnections.contains(facing) &&
                to instanceof IPipe && this.getType().getNature() == ((IPipe<?>) to).getType().getNature() &&
                this.getType().getSize() == ((IPipe<?>) to).getType().getSize();
    }

    @Override
    public G createGrid(final int nextID)
    {
        return null;
    }

    @Override
    public BlockPos getBlockPos()
    {
        return this.getPos();
    }

    @Override
    public World getBlockWorld()
    {
        return this.getWorld();
    }

    public void forbidConnection(EnumFacing facing, boolean forbidden)
    {
        if (facing == null)
        {
            this.syncLock();

            for (EnumFacing side : EnumFacing.VALUES)
            {
                if (this.forbiddenSwitch && this.isConnectionForbidden(side))
                    this.forbidConnection(side, false);
                else if (!this.forbiddenSwitch && this.isConnected(side))
                    this.forbidConnection(side, true);
            }
            this.forbiddenSwitch = !this.forbiddenSwitch;

            this.releaseSyncLock(true);
            return;
        }

        if (forbidden)
        {
            this.forbiddenConnections.add(facing);

            if (this.getConnectionsMap().containsKey(facing))
            {
                this.getConnected(facing).disconnect(facing.getOpposite());
                this.disconnect(facing);

                CableGrid newGrid = GridManager.getInstance().addGrid(this.getGridObject().copy(
                        GridManager.getInstance().getNextID()));

                if (this.getConnections().length == 0)
                {
                    this.getGridObject().removeCable(this);

                    this.setGrid(newGrid.getIdentifier());
                    newGrid.addCable(this);
                }
                else
                {
                    GridManager.getInstance().getOrphans(this.getGridObject(), this).forEach(orphan ->
                    {
                        this.getGridObject().removeCable(orphan);

                        orphan.setGrid(newGrid.getIdentifier());
                        newGrid.addCable(orphan);
                    });
                }
            }
            if (this.adjacentHandler.containsKey(facing))
                this.disconnectHandler(facing, this.world.getTileEntity(this.getAdjacentPos(facing)));
        }
        else
        {
            this.forbiddenConnections.remove(facing);

            TileEntity node = this.world.getTileEntity(this.getAdjacentPos(facing));
            if (node instanceof ITileCable && this.canConnect(facing, (ITileNode<?>) node) &&
                    ((ITileCable) node).canConnect(facing.getOpposite(), this))
            {
                if (this.getGridObject().canMerge(((ITileCable) node).getGridObject()))
                {
                    if (this.getConnections().length == 0)
                        GridManager.getInstance().mergeGrids(((ITileCable) node).getGridObject(), this.getGridObject());
                    else
                        GridManager.getInstance().mergeGrids(this.getGridObject(), ((ITileCable) node).getGridObject());
                }
                else if (((ITileCable) node).getGrid() != this.getGrid())
                    return;

                ((ITileCable) node).connect(facing.getOpposite(), this);
                this.connect(facing, (ITileCable<G>) node);

                this.updateState();
                ((ITileCable<G>) node).updateState();
            }
            else
                this.scanHandler(facing);
        }
    }

    public boolean isConnectionForbidden(EnumFacing facing)
    {
        return this.forbiddenConnections.contains(facing);
    }

    ////////////
    // RENDER //
    ////////////

    public ROSOBJState getVisibilityState()
    {
        return PipeOBJStates.getVisibilityState(this.renderConnections.toArray(new EnumFacing[0]));
    }

    public void updateState()
    {
        if (this.isServer())
        {
            this.sync();
            return;
        }

        this.world.markBlockRangeForRenderUpdate(this.getPos(), this.getPos());
    }

    public boolean isConnected(final EnumFacing facing)
    {
        if (this.isClient())
            return this.renderConnections.contains(facing);
        return this.connectionsMap.containsKey(facing) || this.adjacentHandler.containsKey(facing);
    }

    public NBTTagCompound writeRenderConnections(NBTTagCompound tag)
    {
        for (Entry<EnumFacing, ITileCable<G>> entry : this.connectionsMap.entrySet())
            tag.setBoolean("connected" + entry.getKey().ordinal(), true);
        for (Entry<EnumFacing, H> entry : this.adjacentHandler.entrySet())
            tag.setBoolean("connected" + entry.getKey().ordinal(), true);
        return tag;
    }

    public boolean readRenderConnections(NBTTagCompound tag)
    {
        int previousConnections = this.renderConnections.size();

        this.renderConnections.clear();
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (tag.hasKey("connected" + facing.ordinal()))
                this.renderConnections.add(facing);
        }
        return this.renderConnections.size() != previousConnections;
    }
}
