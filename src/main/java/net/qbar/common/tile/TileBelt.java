package net.qbar.common.tile;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.BeltGrid;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.ITileCable;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamUtil;

public class TileBelt extends TileInventoryBase
        implements ITileCable<BeltGrid>, ITileInfoProvider, ISidedInventory, ILoadable
{
    private int   gridID;
    private float beltSpeed;

    public TileBelt(final float beltSpeed)
    {
        super("InventoryBelt", 4);

        this.beltSpeed = beltSpeed;

        this.gridID = -1;
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

        tag.setFloat("beltSpeed", this.beltSpeed);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.beltSpeed = tag.getFloat("beltSpeed");
    }

    @Override
    public void addInfo(final List<String> lines)
    {
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

        lines.add("Slot 1: " + this.getStackInSlot(0));
        lines.add("Slot 2: " + this.getStackInSlot(1));
        lines.add("Slot 3: " + this.getStackInSlot(2));
        lines.add("Slot 4: " + this.getStackInSlot(3));
    }

    @Override
    public EnumFacing[] getConnections()
    {
        return new EnumFacing[0];
    }

    @Override
    public ITileCable<BeltGrid> getConnected(final EnumFacing facing)
    {
        return null;
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
                // if (this.coldStorage != null)
                // {
                // if (grid.getFluid() == null ||
                // grid.getFluid().equals(this.coldStorage.getFluid()))
                // return true;
                // return false;
                // }
            }
            return true;
        }
        return false;
    }

    @Override
    public void connect(final EnumFacing facing, final ITileCable<BeltGrid> to)
    {

    }

    @Override
    public void disconnect(final EnumFacing facing)
    {

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
        return new ItemStack[] { new ItemStack(Items.APPLE, 1), new ItemStack(Blocks.GOLD_BLOCK, 1) };
    }

    public Vec2f[] getItemPositions()
    {
        return new Vec2f[] { new Vec2f(11f / 32f, 7 / 16f), new Vec2f(11f / 32f, 0 / 16f) };
    }
}
