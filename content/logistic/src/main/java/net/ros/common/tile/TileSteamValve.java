package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.grid.GridManager;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.steam.SteamUtil;

import javax.annotation.Nullable;
import java.util.List;

public class TileSteamValve extends TileSteamPipe implements IPipeValve
{
    @Getter
    private boolean isOpen;

    public TileSteamValve(PipeType type, int transferCapacity, float maxPressure)
    {
        super(type, transferCapacity, maxPressure);
    }

    public TileSteamValve()
    {
        this(null, 0, 0);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.isOpen = tag.getBoolean("isOpen");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setBoolean("isOpen", this.isOpen);

        return super.writeToNBT(tag);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        super.addInfo(lines);

        lines.add("Facing: " + this.getFacing());
        lines.add("Open: " + this.isOpen());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == this.capability)
            return facing != this.getFacing();
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == this.capability && facing != this.getFacing())
        {
            if (this.isOpen())
                return SteamCapabilities.STEAM_HANDLER.cast(this.getGridObject().getTank());
            else
                return SteamCapabilities.STEAM_HANDLER.cast(SteamUtil.EMPTY_TANK);
        }
        return super.getCapability(capability, facing);
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockDirectional.FACING);
    }

    @Override
    public void setOpen(boolean isOpen)
    {
        boolean previous = this.isOpen;
        this.isOpen = isOpen;

        if (previous != isOpen)
        {
            if (isOpen)
                GridManager.getInstance().connectCable(this);
            else
            {
                this.disconnectItself();

                for (EnumFacing facing : EnumFacing.VALUES)
                {
                    if (!this.isConnected(facing))
                        continue;

                    this.disconnect(facing);
                }
                this.setGrid(-1);
                GridManager.getInstance().connectCable(this);
            }
            this.sync();
        }
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        if (facing == this.getFacing())
            return false;

        return this.isOpen && super.canConnect(facing, to);
    }

    @Override
    protected boolean keepAsValve(EnumFacing facing, TileEntity tile)
    {
        if (tile == null || facing == this.getFacing())
            return false;
        if (tile instanceof TileSteamValve && !((TileSteamValve) tile).isOpen())
        {
            if (((TileSteamValve) tile).getFacing().getOpposite() == facing ||
                    ((TileSteamValve) tile).isConnectionForbidden(facing.getOpposite()))
                return false;
            return !this.isOpen();
        }
        if (tile instanceof TileSteamPipe)
        {
            if (((TileSteamPipe) tile).canConnect(facing.getOpposite(), this))
                return !this.isOpen();
            return false;
        }
        if (tile.hasCapability(this.capability, facing))
            return !this.isOpen();
        return false;
    }
}
