package net.qbar.common.tile.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.client.render.model.obj.QBarOBJState;
import net.qbar.common.block.BlockBelt.EBeltSlope;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.*;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.ILoadable;
import net.qbar.common.tile.QBarTileBase;

import javax.vecmath.Vector2f;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class TileBelt extends QBarTileBase implements IBelt, ILoadable, IConnectionAware
{
    private int                                             gridID;
    private final EnumMap<EnumFacing, ITileCable<BeltGrid>> connections;
    private float                                           beltSpeed;

    private EnumFacing                                      facing;

    private final List<ItemBelt>                            items;

    private boolean                                         hasChanged = false;
    private boolean                                         isWorking  = false;

    private final EnumMap<EnumFacing, ISteamHandler>        steamConnections;

    private EBeltSlope                                      slopeState;

    public TileBelt(final float beltSpeed)
    {
        this.beltSpeed = beltSpeed;

        this.gridID = -1;
        this.connections = new EnumMap<>(EnumFacing.class);
        this.steamConnections = new EnumMap<>(EnumFacing.class);
        this.facing = EnumFacing.UP;

        this.items = new ArrayList<>(3);
        this.slopeState = EBeltSlope.NORMAL;
    }

    public TileBelt()
    {
        this(0);
    }

    @Override
    public boolean hasFastRenderer()
    {
        return true;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && facing.getAxis().isHorizontal())
            return this.getGrid() != -1 && this.getGridObject() != null;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && facing.getAxis().isHorizontal())
            return (T) this.getGridObject().getTank();
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("facing", this.facing.ordinal());
        tag.setFloat("beltSpeed", this.beltSpeed);
        tag.setInteger("isSlope", this.slopeState.ordinal());

        for (final ItemBelt belt : this.items)
        {
            final NBTTagCompound subTag = new NBTTagCompound();

            subTag.setFloat("posX", belt.getPos().x);
            subTag.setFloat("posY", belt.getPos().y);

            belt.getStack().writeToNBT(subTag);

            tag.setTag("item" + this.items.indexOf(belt), subTag);
        }
        tag.setInteger("itemCount", this.items.size());
        tag.setBoolean("isWorking", this.isWorking);

        for (final Entry<EnumFacing, ISteamHandler> entry : this.steamConnections.entrySet())
            tag.setBoolean("connectedSteam" + entry.getKey().ordinal(), true);
        for (final Entry<EnumFacing, ITileCable<BeltGrid>> entry : this.connections.entrySet())
            tag.setBoolean("connected" + entry.getKey().ordinal(), true);
        return tag;
    }

    private final EnumMap<EnumFacing, ITileCable<BeltGrid>> tmpConnections      = new EnumMap<>(EnumFacing.class);
    private final EnumMap<EnumFacing, ISteamHandler>        tmpSteamConnections = new EnumMap<>(EnumFacing.class);

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
        this.beltSpeed = tag.getFloat("beltSpeed");
        this.slopeState = EBeltSlope.values()[tag.getInteger("isSlope")];

        this.items.clear();
        for (int i = 0; i < tag.getInteger("itemCount"); i++)
        {
            final NBTTagCompound subTag = tag.getCompoundTag("item" + i);
            this.items.add(new ItemBelt(new ItemStack(subTag),
                    new Vector2f(subTag.getFloat("posX"), subTag.getFloat("posY"))));
        }

        boolean needStateUpdate = false;
        if (this.isWorking != tag.getBoolean("isWorking"))
            needStateUpdate = true;
        this.isWorking = tag.getBoolean("isWorking");

        if (this.isClient())
        {
            this.tmpConnections.clear();
            this.tmpConnections.putAll(this.connections);
            this.connections.clear();

            this.tmpSteamConnections.clear();
            this.tmpSteamConnections.putAll(this.steamConnections);
            this.steamConnections.clear();

            for (final EnumFacing facing : EnumFacing.VALUES)
            {
                if (tag.hasKey("connectedSteam" + facing.ordinal()))
                    this.steamConnections.put(facing, null);
                if (tag.hasKey("connected" + facing.ordinal()))
                    this.connections.put(facing, null);
            }
            if (!this.tmpConnections.equals(this.connections)
                    || !this.tmpSteamConnections.equals(this.steamConnections))
                needStateUpdate = true;

            if (needStateUpdate)
                this.updateState();
        }
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Slope: " + this.slopeState);
        lines.add("Orientation: " + this.getFacing());
        lines.add("Grid: " + this.getGrid());

        if (this.getGrid() != -1 && this.getGridObject() != null)
        {
            lines.add("Contains: " + this.getGridObject().getTank().getSteam() + " / "
                    + this.getGridObject().getTank().getCapacity());
            lines.add("Pressure " + SteamUtil.pressureFormat.format(this.getGridObject().getTank().getPressure())
                    + " / " + SteamUtil.pressureFormat.format(this.getGridObject().getTank().getMaxPressure()));
        }
        else
            lines.add("Errored grid!");

        for (final ItemBelt item : this.items)
            lines.add("Slot " + this.items.indexOf(item) + ": " + item.getStack());
    }

    @Override
    public EnumFacing[] getConnections()
    {
        return this.connections.keySet().toArray(new EnumFacing[0]);
    }

    @Override
    public ITileCable<BeltGrid> getConnected(final EnumFacing facing)
    {
        return this.connections.get(facing);
    }

    @Override
    public int getGrid()
    {
        return this.gridID;
    }

    @Override
    public void setGrid(final int gridIdentifier)
    {
        this.gridID = gridIdentifier;

        if (this.getGridObject() != null)
            this.world.notifyNeighborsOfStateChange(this.getBlockPos(), this.getBlockType(), true);
    }

    @Override
    public void onChunkUnload()
    {
        GridManager.getInstance().disconnectCable(this);
    }

    @Override
    public boolean canConnect(final ITileCable<?> to)
    {
        if (to instanceof TileBelt)
        {
            final IBelt adjacentBelt = (IBelt) to;
            if (adjacentBelt.getFacing() != this.getFacing().getOpposite())
                return true;
            return false;
        }
        return false;
    }

    @Override
    public void connect(final EnumFacing facing, final ITileCable<BeltGrid> to)
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

    @Override
    public BeltGrid createGrid(final int nextID)
    {
        return new BeltGrid(nextID, this.beltSpeed);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (this.isServer() && this.getGrid() == -1)
            TickHandler.loadables.add(this);
        if (this.isClient())
        {
            this.forceSync();
            this.updateState();
        }
    }

    @Override
    public void load()
    {
        GridManager.getInstance().connectCable(this);
    }

    @Override
    public BlockPos getAdjacentPos(final EnumFacing facing)
    {
        if (this.slopeState.equals(EBeltSlope.DOWN) && facing == this.getFacing())
            return this.getBlockPos().down().offset(facing);
        else if (this.slopeState.equals(EBeltSlope.UP) && facing == this.getFacing().getOpposite())
            return this.getBlockPos().down().offset(facing);
        return this.getBlockPos().offset(facing);
    }

    @Override
    public void adjacentConnect()
    {
        for (final EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            final TileEntity adjacent = this.getBlockWorld().getTileEntity(this.getAdjacentPos(facing).up());
            if (adjacent != null && adjacent instanceof IBelt && ((IBelt) adjacent).isSlope()
                    && this.canConnect((IBelt) adjacent) && ((IBelt) adjacent).canConnect(this))
            {
                this.connect(facing, (IBelt) adjacent);
                ((IBelt) adjacent).connect(facing.getOpposite(), this);
            }
        }

        for (final EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            if (this.isConnected(facing))
                continue;
            final TileEntity adjacent = this.getBlockWorld().getTileEntity(this.getAdjacentPos(facing));
            if (adjacent != null && adjacent instanceof IBelt && this.canConnect((IBelt) adjacent)
                    && ((IBelt) adjacent).canConnect(this))
            {
                this.connect(facing, (IBelt) adjacent);
                ((IBelt) adjacent).connect(facing.getOpposite(), this);
            }
        }
    }

    public float getBeltSpeed()
    {
        return this.beltSpeed;
    }

    @Override
    public List<ItemBelt> getItems()
    {
        return this.items;
    }

    @Override
    public EnumFacing getFacing()
    {
        return this.facing;
    }

    public void setFacing(final EnumFacing facing)
    {
        this.facing = facing;
    }

    @Override
    public boolean isSlope()
    {
        return this.slopeState.isSlope();
    }

    public EBeltSlope getSlopeState()
    {
        return this.slopeState;
    }

    public void setSlope(final EBeltSlope slope)
    {
        this.slopeState = slope;
    }

    @Override
    public boolean insert(final ItemStack stack, final boolean doInsert)
    {
        if (this.getGridObject() != null)
            return this.getGridObject().insert(this, stack, doInsert);
        return false;
    }

    @Override
    public void itemUpdate()
    {
        this.sync();
    }

    @Override
    public boolean hasChanged()
    {
        return this.hasChanged;
    }

    @Override
    public void setChanged(final boolean change)
    {
        this.hasChanged = change;
    }

    @Override
    public boolean isWorking()
    {
        return this.isWorking;
    }

    @Override
    public void setWorking(final boolean working)
    {
        this.isWorking = working;
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
        if (this.getFacing().getAxis().isVertical())
            return "vertical";

        StringBuilder rtn = new StringBuilder(2);

        if (this.isConnected(this.getFacing().rotateY()))
            rtn.append("e");
        if (this.isConnected(this.getFacing().rotateY().getOpposite()))
            rtn.append("w");
        return rtn.toString();
    }

    private QBarOBJState buildVisibilityState()
    {
        List<String> parts = new ArrayList<>();

        if (this.getFacing().getAxis().isVertical())
        {
            parts.add("east");
            parts.add("west");
        }
        else
        {
            if (!this.isConnected(this.getFacing().rotateY()))
                parts.add("east");
            if (!this.isConnected(this.getFacing().rotateY().getOpposite()))
                parts.add("west");
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

    public boolean isConnected(final EnumFacing facing)
    {
        return this.connections.containsKey(facing) || this.steamConnections.containsKey(facing);
    }

    public void connectSteam(final EnumFacing facing, final ISteamHandler handler)
    {
        this.steamConnections.put(facing, handler);
        this.updateState();
    }

    public void disconnectSteam(final EnumFacing facing)
    {
        this.steamConnections.remove(facing);
        this.updateState();
    }

    @Override
    public void connectTrigger(final EnumFacing facing, CableGrid grid)
    {
        this.connectSteam(facing, null);
    }

    @Override
    public void disconnectTrigger(final EnumFacing facing, CableGrid grid)
    {
        this.disconnectSteam(facing);
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
