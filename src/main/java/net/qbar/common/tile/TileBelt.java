package net.qbar.common.tile;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector2f;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.client.render.tile.VisibilityModelState;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.BeltGrid;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.IBelt;
import net.qbar.common.grid.IBeltInput;
import net.qbar.common.grid.IConnectionAware;
import net.qbar.common.grid.ITileCable;
import net.qbar.common.grid.ItemBelt;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.steam.SteamUtil;

public class TileBelt extends QBarTileBase implements IBelt, ITileInfoProvider, ILoadable, IConnectionAware
{
    private int                                             gridID;
    private final EnumMap<EnumFacing, ITileCable<BeltGrid>> connections;
    private float                                           beltSpeed;

    private EnumFacing                                      facing;

    private IBeltInput                                      input;

    private final List<ItemBelt>                            items;

    private boolean                                         hasChanged = false;

    private final EnumMap<EnumFacing, ISteamHandler>        steamConnections;

    public TileBelt(final float beltSpeed)
    {
        this.beltSpeed = beltSpeed;

        this.gridID = -1;
        this.connections = new EnumMap<>(EnumFacing.class);
        this.steamConnections = new EnumMap<>(EnumFacing.class);
        this.facing = EnumFacing.UP;

        this.input = null;

        this.items = new ArrayList<>(3);
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
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("facing", this.facing.ordinal());
        tag.setFloat("beltSpeed", this.beltSpeed);

        for (final ItemBelt belt : this.items)
        {
            final NBTTagCompound subTag = new NBTTagCompound();

            subTag.setFloat("posX", belt.getPos().x);
            subTag.setFloat("posY", belt.getPos().y);

            belt.getStack().writeToNBT(subTag);

            tag.setTag("item" + this.items.indexOf(belt), subTag);
        }
        tag.setInteger("itemCount", this.items.size());

        for (final Entry<EnumFacing, ISteamHandler> entry : this.steamConnections.entrySet())
            tag.setBoolean("connectedSteam" + entry.getKey().ordinal(), true);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
        this.beltSpeed = tag.getFloat("beltSpeed");

        this.items.clear();
        for (int i = 0; i < tag.getInteger("itemCount"); i++)
        {
            final NBTTagCompound subTag = tag.getCompoundTag("item" + i);
            this.items.add(new ItemBelt(new ItemStack(subTag),
                    new Vector2f(subTag.getFloat("posX"), subTag.getFloat("posY"))));
        }
        if (this.isClient())
        {
            final int previousSteamHandlers = this.steamConnections.size();
            this.steamConnections.clear();
            for (final EnumFacing facing : EnumFacing.VALUES)
            {
                if (tag.hasKey("connectedSteam" + facing.ordinal()))
                    this.connectSteam(facing, null);
            }

            if (this.steamConnections.size() == 0 && previousSteamHandlers != 0)
                this.updateState();
        }
    }

    @Override
    public void addInfo(final List<String> lines)
    {
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
        lines.add("Connected: " + (this.input != null));

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

        if (this.getGridObject() != null && this.input != null)
            this.getGridObject().addInput(this);
        if (this.getGridObject() != null)
            this.world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true);
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
            final BeltGrid grid = ((TileBelt) to).getGridObject();
            if (grid != null)
            {
                final IBelt adjacentBelt = (IBelt) to;
                if (adjacentBelt.getFacing() != this.getFacing().getOpposite())
                    return true;
            }
            return false;
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

    @Override
    public BeltGrid createGrid(final int nextID)
    {
        return new BeltGrid(nextID, this.beltSpeed);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (!this.world.isRemote && this.getGrid() == -1)
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
        this.scanInput();
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
        return false;
    }

    @Override
    public void connectInput(final BlockPos pos)
    {
        this.input = (IBeltInput) this.world.getTileEntity(pos);

        if (this.getGridObject() != null)
            this.getGridObject().addInput(this);
    }

    public void scanInput()
    {
        final BlockPos search = this.getPos().offset(EnumFacing.UP);
        if (this.input == null)
        {
            if (this.world.getTileEntity(search) != null && this.world.getTileEntity(search) instanceof IBeltInput
                    && ((IBeltInput) this.world.getTileEntity(search)).canInput(this))
            {
                this.input = (IBeltInput) this.world.getTileEntity(search);

                if (this.getGridObject() != null)
                    this.getGridObject().addInput(this);
            }
        }
        else
        {
            if (this.world.getTileEntity(search) == null || !(this.world.getTileEntity(search) instanceof IBeltInput)
                    || !((IBeltInput) this.world.getTileEntity(search)).canInput(this))
            {
                this.input = null;
                if (this.getGridObject() != null)
                    this.getGridObject().removeInput(this);
            }
        }
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

        if (this.getFacing().getAxis().isVertical())
        {
            this.state.hidden.add("east");
            this.state.hidden.add("west");
        }
        else
        {
            if (!this.isSteamConnected(this.getFacing().rotateY()))
                this.state.hidden.add("east");
            if (!this.isSteamConnected(this.getFacing().rotateY().getOpposite()))
                this.state.hidden.add("west");
        }

        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    public boolean isSteamConnected(final EnumFacing facing)
    {
        return this.steamConnections.containsKey(facing);
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
    public void connectTrigger(final EnumFacing facing)
    {
        this.connectSteam(facing, null);
    }

    @Override
    public void disconnectTrigger(final EnumFacing facing)
    {
        this.disconnectSteam(facing);
    }

    public void scanSteam()
    {
        final Iterator<Entry<EnumFacing, ISteamHandler>> iterator = this.steamConnections.entrySet().iterator();

        while (iterator.hasNext())
        {

        }
    }
}
