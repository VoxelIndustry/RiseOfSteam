package net.qbar.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.client.render.model.obj.QBarOBJState;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.CableGrid;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.IConnectionAware;
import net.qbar.common.grid.ITileCable;
import net.qbar.common.network.PipeUpdatePacket;

import java.util.*;
import java.util.Map.Entry;

public class TilePipeBase<G extends CableGrid, H> extends QBarTileBase implements ILoadable, ITileCable<G>
{
    protected final EnumSet<EnumFacing>                renderConnections;

    protected final EnumMap<EnumFacing, ITileCable<G>> connections;
    protected final EnumMap<EnumFacing, H>             adjacentHandler;
    protected final Capability<H>                      capability;
    protected int                                      grid;

    protected int                                      transferCapacity;

    public TilePipeBase(final int transferCapacity, final Capability<H> capability)
    {
        this.transferCapacity = transferCapacity;
        this.capability = capability;

        this.connections = new EnumMap<>(EnumFacing.class);
        this.adjacentHandler = new EnumMap<>(EnumFacing.class);
        this.grid = -1;

        this.renderConnections = EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == this.capability)
            return this.getGrid() != -1 && this.getGridObject() != null;
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
        this.connections.forEach((facing, cable) -> lines.add("Pipe " + facing + ": " + (cable != null)));
        this.adjacentHandler.forEach((facing, handler) -> lines.add("Handler " + facing + ": " + (handler != null)));
    }

    public void addSpecificInfo(final List<String> lines)
    {
    }

    @Override
    public EnumFacing[] getConnections()
    {
        return this.connections.keySet().toArray(new EnumFacing[0]);
    }

    public Collection<H> getConnectedHandlers()
    {
        return this.adjacentHandler.values();
    }

    @Override
    public ITileCable<G> getConnected(final EnumFacing facing)
    {
        return this.connections.get(facing);
    }

    @Override
    public int getGrid()
    {
        return this.grid;
    }

    @Override
    public void connect(final EnumFacing facing, final ITileCable<G> to)
    {
        this.connections.put(facing, to);
    }

    @Override
    public void disconnect(final EnumFacing facing)
    {
        this.connections.remove(facing);
        this.updateState();
    }

    public void connectHandler(final EnumFacing facing, final H to, final TileEntity tile)
    {
        this.adjacentHandler.put(facing, to);
        this.updateState();

        if (tile != null && tile instanceof IConnectionAware)
            ((IConnectionAware) tile).connectTrigger(facing.getOpposite(), this.getGridObject());
    }

    public void disconnectHandler(final EnumFacing facing, final TileEntity tile)
    {
        this.adjacentHandler.remove(facing);
        this.updateState();

        if (tile != null && tile instanceof IConnectionAware)
            ((IConnectionAware) tile).disconnectTrigger(facing.getOpposite(), this.getGridObject());
    }

    public void disconnectItself()
    {
        GridManager.getInstance().disconnectCable(this);

        this.adjacentHandler.keySet().forEach(facing ->
        {
            final TileEntity handler = this.getBlockWorld().getTileEntity(this.getBlockPos().offset(facing));
            if (handler != null && handler instanceof IConnectionAware)
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
            this.forceSync();
        }
    }

    @Override
    public void load()
    {
        GridManager.getInstance().connectCable(this);
        for (final EnumFacing facing : EnumFacing.VALUES)
            this.scanHandlers(this.pos.offset(facing));
    }

    @Override
    public void readFromNBT(final NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);

        this.transferCapacity = tagCompound.getInteger("transferCapacity");

        final int previousConnections = this.renderConnections.size();

        if (this.isClient())
        {
            this.renderConnections.clear();
            for (final EnumFacing facing : EnumFacing.VALUES)
            {
                if (tagCompound.hasKey("connected" + facing.ordinal()))
                    this.renderConnections.add(facing);
            }
            if (this.renderConnections.size() != previousConnections)
                this.updateState();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);

        tagCompound.setInteger("transferCapacity", this.transferCapacity);

        if (this.isServer())
        {
            for (final Entry<EnumFacing, ITileCable<G>> entry : this.connections.entrySet())
                tagCompound.setBoolean("connected" + entry.getKey().ordinal(), true);
            for (final Entry<EnumFacing, H> entry : this.adjacentHandler.entrySet())
                tagCompound.setBoolean("connected" + entry.getKey().ordinal(), true);
        }
        return tagCompound;
    }

    public void scanHandlers(final BlockPos posNeighbor)
    {
    }

    ////////////
    // RENDER //
    ////////////

    private static final HashMap<String, QBarOBJState> variants = new HashMap<>();

    public QBarOBJState getVisibilityState()
    {
        String key = this.getVariantKey();

        if (!this.variants.containsKey(key))
            this.variants.put(key, buildVisibilityState());
        return this.variants.get(key);
    }

    public String getVariantKey()
    {
        StringBuilder rtn = new StringBuilder(12);

        if (this.isConnected(EnumFacing.EAST))
            rtn.append("x+");
        if (this.isConnected(EnumFacing.WEST))
            rtn.append("x-");
        if (this.isConnected(EnumFacing.UP))
            rtn.append("y+");
        if (this.isConnected(EnumFacing.DOWN))
            rtn.append("y-");
        if (this.isConnected(EnumFacing.SOUTH))
            rtn.append("z+");
        if (this.isConnected(EnumFacing.NORTH))
            rtn.append("z-");
        return rtn.toString();
    }

    private QBarOBJState buildVisibilityState()
    {
        List<String> parts = new ArrayList<>();

        if (this.renderConnections.isEmpty())
        {
            parts.add("armx1");
            parts.add("armx2");
            parts.add("army1");
            parts.add("army2");
            parts.add("armz1");
            parts.add("armz2");
            parts.add("straightx");
            parts.add("straighty");
            parts.add("straightz");
        }
        else if (this.isStraight())
        {
            parts.add("center");
            parts.add("armx1");
            parts.add("armx2");

            parts.add("army1");
            parts.add("army2");

            parts.add("armz1");
            parts.add("armz2");

            if (this.isConnected(EnumFacing.WEST))
            {
                parts.add("straighty");
                parts.add("straightz");
            }
            else if (this.isConnected(EnumFacing.NORTH))
            {
                parts.add("straighty");
                parts.add("straightx");
            }
            else if (this.isConnected(EnumFacing.UP))
            {
                parts.add("straightx");
                parts.add("straightz");
            }
        }
        else
        {
            parts.add("straightx");
            parts.add("straighty");
            parts.add("straightz");

            if (!this.isConnected(EnumFacing.UP))
                parts.add("army1");
            if (!this.isConnected(EnumFacing.DOWN))
                parts.add("army2");
            if (!this.isConnected(EnumFacing.NORTH))
                parts.add("armz1");
            if (!this.isConnected(EnumFacing.SOUTH))
                parts.add("armz2");
            if (!this.isConnected(EnumFacing.EAST))
                parts.add("armx1");
            if (!this.isConnected(EnumFacing.WEST))
                parts.add("armx2");
        }
        return new QBarOBJState(parts, false);
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

    public boolean isStraight()
    {
        if (this.renderConnections.size() == 2)
            return this.isConnected(EnumFacing.NORTH) && this.isConnected(EnumFacing.SOUTH)
                    || this.isConnected(EnumFacing.WEST) && this.isConnected(EnumFacing.EAST)
                    || this.isConnected(EnumFacing.UP) && this.isConnected(EnumFacing.DOWN);
        return false;
    }

    public boolean isConnected(final EnumFacing facing)
    {
        return this.renderConnections.contains(facing);
    }

    public NBTTagCompound writeRenderConnections(NBTTagCompound tag)
    {
        for (final Entry<EnumFacing, ITileCable<G>> entry : this.connections.entrySet())
            tag.setBoolean("connected" + entry.getKey().ordinal(), true);
        for (final Entry<EnumFacing, H> entry : this.adjacentHandler.entrySet())
            tag.setBoolean("connected" + entry.getKey().ordinal(), true);
        return tag;
    }

    public void readRenderConnections(NBTTagCompound tag)
    {
        this.renderConnections.clear();
        for (final EnumFacing facing : EnumFacing.VALUES)
        {
            if (tag.hasKey("connected" + facing.ordinal()))
                this.renderConnections.add(facing);
        }
    }

    @Override
    public void adjacentConnect()
    {
        List<TilePipeBase> adjacents = new ArrayList<>(6);
        for (final EnumFacing facing : EnumFacing.VALUES)
        {
            final TileEntity adjacent = this.getBlockWorld().getTileEntity(this.getAdjacentPos(facing));
            if (adjacent != null && adjacent instanceof TilePipeBase && this.canConnect((ITileCable<?>) adjacent)
                    && ((ITileCable<?>) adjacent).canConnect(this))
            {
                this.connect(facing, (TilePipeBase) adjacent);
                ((TilePipeBase) adjacent).connect(facing.getOpposite(), this);
                adjacents.add((TilePipeBase) adjacent);
            }
        }
        new PipeUpdatePacket(this, adjacents).sendToAllIn(this.getWorld());
    }

    @Override
    public void setGrid(final int gridIdentifier)
    {

    }

    @Override
    public boolean canConnect(final ITileCable<?> to)
    {
        return false;
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
}
