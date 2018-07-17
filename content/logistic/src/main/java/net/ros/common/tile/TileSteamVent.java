package net.ros.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.steam.SteamTank;

import javax.annotation.Nullable;

public class TileSteamVent extends TileSteamPipe implements IContainerProvider
{
    @Getter
    @Setter
    private float ventPressure;

    public TileSteamVent(PipeType type)
    {
        super(type);

        this.setVentPressure(0.5f);
    }

    public TileSteamVent()
    {
        this(null);
    }

    @Override
    protected SteamTank createSteamTank(int capacity, float maxPressure)
    {
        return new SteamVentTank(this, capacity, maxPressure);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.setVentPressure(tag.getFloat("ventPressure"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("ventPressure", this.getVentPressure());

        return super.writeToNBT(tag);
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        list.addText("Facing: " + this.getFacing());
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

    @Override
    public boolean canConnect(EnumFacing facing, ITileNode<?> to)
    {
        if (facing == this.getFacing())
            return false;

        return super.canConnect(facing, to);
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation("gui.steamvent.name");
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("steamvent", player)
                .player(player).inventory(8, 107).hotbar(8, 165)
                .addInventory()
                .syncFloatValue(this::getVentPressure, this::setVentPressure)
                .syncIntegerValue(this.getBufferTank()::getSteam, this.getBufferTank()::setSteam)
                .create();
    }

    private class SteamVentTank extends SteamTank
    {
        private TileSteamVent vent;

        SteamVentTank(TileSteamVent vent, int capacity, float maxPressure)
        {
            super(capacity, maxPressure);

            this.vent = vent;
        }

        @Override
        public int fillInternal(final int amount, final boolean doFill)
        {
            int filled = amount;

            filled = (int) Math.min(filled, this.getCapacity() * this.getMaxPressure() - this.getSteam());
            if (doFill)
            {
                int allowed = 0;
                int steamDiff = this.getSteamDifference(vent.getVentPressure());

                if (steamDiff < 0)
                {
                    if (-steamDiff >= filled)
                        allowed = filled;
                    else
                        allowed = -steamDiff;
                }

                this.setSteam(this.getSteam() + allowed);
            }
            return filled;
        }
    }
}
