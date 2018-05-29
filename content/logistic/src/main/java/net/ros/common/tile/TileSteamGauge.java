package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;
import net.ros.common.steam.SteamCapabilities;

import javax.annotation.Nullable;
import java.util.List;

public class TileSteamGauge extends TileSteamPipe implements ITickable
{
    @Getter
    private float currentPressure;

    public TileSteamGauge(PipeType type, int transferCapacity, float maxPressure)
    {
        super(type, transferCapacity, maxPressure);
    }

    public TileSteamGauge()
    {
        this(null, 0, 0);
    }

    @Override
    public void update()
    {
        if (this.isClient() || !this.hasGrid())
            return;

        if (this.world.getTotalWorldTime() % 10 == 0)
        {
            float newPressure = this.getBufferTank().getPressure();

            if (newPressure != currentPressure)
            {
                this.currentPressure = newPressure;
                this.sync();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.currentPressure = tag.getFloat("currentPressure");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("currentPressure", this.currentPressure);

        return super.writeToNBT(tag);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        super.addInfo(lines);

        lines.add("Facing: " + this.getFacing());
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
            return SteamCapabilities.STEAM_HANDLER.cast(this.getGridObject().getTank());
        return super.getCapability(capability, facing);
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockDirectional.FACING);
    }

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        if (facing == this.getFacing())
            return false;

        return super.canConnect(facing, to);
    }
}
