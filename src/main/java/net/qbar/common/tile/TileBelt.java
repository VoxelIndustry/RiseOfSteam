package net.qbar.common.tile;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.BeltGrid;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.IBelt;
import net.qbar.common.grid.IBeltInput;
import net.qbar.common.grid.ITileCable;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamUtil;

public class TileBelt extends TileInventoryBase implements IBelt, ITileInfoProvider, ISidedInventory, ILoadable
{
    private int                                             gridID;
    private final EnumMap<EnumFacing, ITileCable<BeltGrid>> connections;
    private float                                           beltSpeed;

    private EnumFacing                                      facing;

    private IBeltInput                                      input;

    public TileBelt(final float beltSpeed)
    {
        super("InventoryBelt", 3);

        this.beltSpeed = beltSpeed;

        this.gridID = -1;
        this.connections = new EnumMap<>(EnumFacing.class);
        this.facing = EnumFacing.UP;

        this.input = null;
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
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
        this.beltSpeed = tag.getFloat("beltSpeed");
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

        lines.add("Slot 1: " + this.getStackInSlot(0));
        lines.add("Slot 2: " + this.getStackInSlot(1));
        lines.add("Slot 3: " + this.getStackInSlot(2));
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
    }

    @Override
    public void load()
    {
        GridManager.getInstance().connectCable(this);
    }

    public float getBeltSpeed()
    {
        return this.beltSpeed;
    }

    @Override
    public int[] getSlotsForFace(final EnumFacing side)
    {
        if (side.equals(EnumFacing.UP))
            return new int[] { 0, 1, 2, 3 };
        return new int[0];
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction)
    {
        if (direction.equals(EnumFacing.UP))
            return true;
        return false;
    }

    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction)
    {
        return false;
    }

    public ItemStack[] getItems()
    {
        return new ItemStack[] { this.getStackInSlot(0), this.getStackInSlot(1) };
    }

    public Vec2f[] getItemPositions()
    {
        return new Vec2f[] { new Vec2f(11f / 32f, 7 / 16f), new Vec2f(11f / 32f, 0 / 16f) };
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
    public void extractItems()
    {
        for (final ItemStack stack : this.input.inputItems())
        {
            int i;
            for (i = 0; i < 2; i++)
            {
                if (this.getStackInSlot(i).isEmpty())
                {
                    this.setInventorySlotContents(i, stack.copy());
                    this.sync();
                }
            }
        }
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
}
