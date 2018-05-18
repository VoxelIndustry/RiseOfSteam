package net.ros.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.grid.impl.SteamGrid;
import net.ros.common.grid.node.ISteamPipe;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.steam.ISteamHandler;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.steam.SteamUtil;

import java.util.List;

public class TileSteamPipe extends TilePipeBase<SteamGrid, ISteamHandler> implements ISteamPipe
{
    private float maxPressure;

    public TileSteamPipe(final int transferCapacity, float maxPressure)
    {
        super(transferCapacity, SteamCapabilities.STEAM_HANDLER);

        this.maxPressure = maxPressure;
    }

    public TileSteamPipe()
    {
        this(0, 0);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.maxPressure = tag.getFloat("maxPressure");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setFloat("maxPressure", this.maxPressure);

        return tag;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == this.capability)
            return (T) this.getGridObject().getTank();
        return super.getCapability(capability, facing);
    }

    @Override
    public void addSpecificInfo(final List<String> lines)
    {
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.getGridObject().getTank().getPressure()) + " / "
                + SteamUtil.pressureFormat.format(maxPressure));
    }

    @Override
    public void setGrid(final int gridIdentifier)
    {
        this.grid = gridIdentifier;

        if (this.getGridObject() != null && !this.adjacentHandler.isEmpty())
            this.adjacentHandler.forEach((facing, handler) -> this.getGridObject().addConnectedPipe(this, handler));
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        return to instanceof TileSteamPipe && super.canConnect(facing, to);
    }

    @Override
    public void scanHandler(final BlockPos posNeighbor)
    {
        final TileEntity tile = this.world.getTileEntity(posNeighbor);

        final BlockPos substracted = posNeighbor.subtract(this.pos);
        final EnumFacing facing = EnumFacing.getFacingFromVector(
                substracted.getX(), substracted.getY(), substracted.getZ()).getOpposite();

        if (this.adjacentHandler.containsKey(facing.getOpposite()))
        {
            if (tile == null || !tile.hasCapability(this.capability, facing))
            {
                if (this.getGridObject() != null)
                    this.getGridObject().removeConnectedPipe(this, this.adjacentHandler.get(facing.getOpposite()));

                this.disconnectHandler(facing.getOpposite(), tile);
            }
            else if (tile.hasCapability(this.capability, facing) &&
                    !tile.getCapability(this.capability, facing).equals(this.adjacentHandler.get(facing.getOpposite())))
            {
                this.connectHandler(facing.getOpposite(), tile.getCapability(this.capability, facing), tile);

                if (this.getGridObject() != null)
                    this.getGridObject().addConnectedPipe(this, this.adjacentHandler.get(facing.getOpposite()));
            }
        }
        else
        {
            if (tile != null)
            {
                if (tile.hasCapability(this.capability, facing) && !(tile instanceof TileSteamPipe))
                {
                    this.connectHandler(facing.getOpposite(), tile.getCapability(this.capability, facing), tile);

                    if (this.getGridObject() != null)
                        this.getGridObject().addConnectedPipe(this, this.adjacentHandler.get(facing.getOpposite()));
                }
            }
        }
    }

    @Override
    public void scanValve(EnumFacing facing)
    {
        if (this.forbiddenConnections.contains(facing))
            return;
        TileEntity tile = this.getWorld().getTileEntity(pos.offset(facing));

        if (!this.keepAsValve(facing, tile))
        {
            this.valveOverrides.remove(facing);
            this.updateState();
            return;
        }

        valveOverrides.add(facing);
        this.updateState();
    }

    protected boolean keepAsValve(EnumFacing facing, TileEntity tile)
    {
        if (tile == null)
            return false;
        if (tile instanceof TileSteamValve && !((TileSteamValve) tile).isOpen())
            return ((TileSteamValve) tile).getFacing().getOpposite() != facing;
        return tile instanceof TileSteamPipe || tile.hasCapability(this.capability, facing);
    }

    @Override
    public SteamGrid createGrid(final int id)
    {
        return new SteamGrid(id, this.transferCapacity, this.maxPressure);
    }
}
