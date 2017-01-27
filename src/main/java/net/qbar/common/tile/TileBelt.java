package net.qbar.common.tile;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.qbar.common.grid.BeltGrid;
import net.qbar.common.grid.ITileCable;

public class TileBelt extends TileInventoryBase implements ITileCable<BeltGrid>, ITileInfoProvider
{
    private int   gridID;
    private float beltSpeed;

    public TileBelt(final float beltSpeed)
    {
        super("InventoryBelt", 4);

        this.beltSpeed = beltSpeed;
    }

    public TileBelt()
    {
        this(0);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("gridID", this.gridID);
        tag.setFloat("beltSpeed", this.beltSpeed);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.gridID = tag.getInteger("gridID");
        this.beltSpeed = tag.getFloat("beltSpeed");
    }

    @Override
    public void addInfo(final List<String> lines)
    {

    }

    @Override
    public EnumFacing[] getConnections()
    {
        return null;
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

    public float getBeltSpeed()
    {
        return this.beltSpeed;
    }
}
