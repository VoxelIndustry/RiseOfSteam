package net.ros.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockDirectional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeSize;
import net.ros.common.grid.node.PipeType;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.IActionReceiver;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.steam.SteamTank;

import javax.annotation.Nullable;

public class TileSteamVent extends TileSteamPipe implements IContainerProvider, IActionReceiver, ITickable
{
    @Getter
    @Setter
    private float   ventPressure;
    private boolean isVenting;

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
    public void update()
    {
        if (this.isClient() && this.isVenting && this.world.getWorldTime() % 2 == 0)
        {
            EnumFacing facing = this.getFacing();

            double offset = this.getType().getSize() == PipeSize.LARGE ? 0.85 : 0.5;

            this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    this.getPos().getX() + 0.5 + facing.getFrontOffsetX() * offset, this.getPos().getY() + 0.85,
                    this.getPos().getZ() + 0.5 + facing.getFrontOffsetZ() * offset,
                    facing.getFrontOffsetX() * 0.03, 0.1, facing.getFrontOffsetZ() * 0.03);
            return;
        }

        if (this.getBufferTank().getPressure() > this.getVentPressure())
        {
            this.getBufferTank().drainSteam(Math.min(this.getTransferRate() / 8,
                    this.getBufferTank().getSteamDifference(this.getVentPressure())), true);

            if (!this.isVenting)
            {
                this.isVenting = true;
                this.sync();
            }
        }
        else if (this.isVenting)
        {
            this.isVenting = false;
            this.sync();
        }
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
        this.isVenting = tag.getBoolean("isVenting");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("ventPressure", this.getVentPressure());
        tag.setBoolean("isVenting", this.isVenting);

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
                .player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory()
                .syncFloatValue(this::getVentPressure, this::setVentPressure)
                .syncIntegerValue(this.getBufferTank()::getSteam, this.getBufferTank()::setSteam)
                .create();
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("ventpressure".equals(actionID))
            this.setVentPressure(payload.getFloat("ventpressure"));
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

                if (allowed != amount && !vent.isVenting)
                {
                    vent.isVenting = true;
                    vent.sync();
                }
                else if (allowed == amount && vent.isVenting)
                {
                    vent.isVenting = false;
                    vent.sync();
                }
            }
            return filled;
        }
    }
}
