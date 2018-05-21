package net.ros.common.tile;

import net.minecraft.block.BlockDirectional;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.steam.SteamCapabilities;

import javax.annotation.Nullable;
import java.util.List;

public class TileSteamGauge extends TileSteamPipe
{
    public TileSteamGauge(final int transferCapacity, float maxPressure)
    {
        super(transferCapacity, maxPressure);
    }

    public TileSteamGauge()
    {
        this(0, 0);
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
