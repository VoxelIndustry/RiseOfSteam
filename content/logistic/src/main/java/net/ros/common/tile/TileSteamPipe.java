package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.grid.impl.SteamGrid;
import net.ros.common.grid.node.ISteamPipe;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;
import net.ros.common.steam.ISteamHandler;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.steam.SteamTank;
import net.ros.common.steam.SteamUtil;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

public class TileSteamPipe extends TilePipeBase<SteamGrid, ISteamHandler> implements ISteamPipe
{
    @Getter
    private float maxPressure;

    @Getter
    private SteamTank bufferTank;

    public TileSteamPipe(PipeType type)
    {
        super(type, PipeType.getTransferRate(type), SteamCapabilities.STEAM_HANDLER);

        this.maxPressure = PipeType.getPressure(type);
        this.bufferTank = this.createSteamTank((int) (this.getTransferRate() * 4), this.maxPressure);
    }

    public TileSteamPipe()
    {
        this(null);
    }

    protected SteamTank createSteamTank(int capacity, float maxPressure)
    {
        return new SteamTank(capacity, maxPressure);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.maxPressure = tag.getFloat("maxPressure");

        this.bufferTank.readFromNBT(tag.getCompoundTag("steamTank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setFloat("maxPressure", this.maxPressure);
        tag.setTag("steamTank", this.bufferTank.writeToNBT(new NBTTagCompound()));

        return tag;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == this.capability)
            return SteamCapabilities.STEAM_HANDLER.cast(this.getBufferTank());
        return super.getCapability(capability, facing);
    }

    @Override
    public void addSpecificInfo(ITileInfoList list)
    {
        list.addText("Steam: " + bufferTank.getSteam());
        list.addText("Pressure: " + SteamUtil.pressureFormat.format(bufferTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(bufferTank.getMaxPressure()));
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
    public SteamGrid createGrid(final int id)
    {
        return new SteamGrid(id, this.getTransferRate(), this.maxPressure);
    }
}
