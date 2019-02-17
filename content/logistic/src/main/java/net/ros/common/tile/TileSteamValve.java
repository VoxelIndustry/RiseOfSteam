package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.grid.node.IPipeValve;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.steam.SteamTank;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

import javax.annotation.Nullable;

public class TileSteamValve extends TileSteamPipe implements IPipeValve
{
    @Getter
    private boolean isOpen;

    public TileSteamValve(PipeType type)
    {
        super(type);
    }

    public TileSteamValve()
    {
        this(null);
    }

    @Override
    protected SteamTank createSteamTank(int capacity, float maxPressure)
    {
        return new SteamTank(capacity, maxPressure)
        {
            private TileSteamValve valve;

            {
                this.valve = TileSteamValve.this;
            }

            @Override
            public int drainSteam(int amount, boolean doDrain)
            {
                if (valve.isOpen())
                    return super.drainSteam(amount, doDrain);
                return 0;
            }

            @Override
            public int fillSteam(int amount, boolean doFill)
            {
                if (valve.isOpen())
                    return super.fillSteam(amount, doFill);
                return 0;
            }
        };
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
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        list.addText("Facing: " + this.getFacing());
        list.addText("Open: " + this.isOpen());
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
            return SteamCapabilities.STEAM_HANDLER.cast(this.getBufferTank());
        return super.getCapability(capability, facing);
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockDirectional.FACING);
    }

    public void setOpen(boolean isOpen)
    {
        this.isOpen = isOpen;
        this.sync();
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        if (facing == this.getFacing())
            return false;

        return super.canConnect(facing, to);
    }
}
