package net.qbar.common.tile;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.common.fluid.DirectionalTank;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;

public class TileBoiler extends QBarTileBase implements ITileInfoProvider
{
    private final DirectionalTank fluidTank;
    private final SteamTank       steamTank;

    public TileBoiler()
    {
        this.fluidTank = new DirectionalTank("TileBoiler", new FluidTank(Fluid.BUCKET_VOLUME * 4), new EnumFacing[0],
                new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST });
        this.steamTank = new SteamTank(0, 0, 4000, SteamUtil.AMBIANT_PRESSURE * 2);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        this.fluidTank.writeToNBT(tag);

        final NBTTagCompound subTag = new NBTTagCompound();
        this.steamTank.writeToNBT(subTag);
        tag.setTag("steamTank", subTag);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.fluidTank.readFromNBT(tag);
        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));

        super.readFromNBT(tag);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return this.fluidTank.canInteract(facing);
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && facing == EnumFacing.UP)
            return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) this.fluidTank.getFluidHandler(facing);
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return (T) this.steamTank;
        return super.getCapability(capability, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        if (this.fluidTank.getFluidHandler(EnumFacing.UP) != null
                && this.fluidTank.getFluidHandler(EnumFacing.UP).getTankProperties()[0].getContents() != null)
        {
            lines.add("Containing " + this.fluidTank.getFluidHandler(EnumFacing.UP).getTankProperties()[0].getContents()
                    .getFluid().getName());
            lines.add(this.fluidTank.getFluidHandler(EnumFacing.UP).getTankProperties()[0].getContents().amount + " / "
                    + this.fluidTank.getFluidHandler(EnumFacing.UP).getTankProperties()[0].getCapacity() + " mB");
        }
        lines.add("Steam " + this.steamTank.getAmount() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + this.steamTank.getPressure() + " / " + this.steamTank.getMaxPressure());
    }

    public DirectionalTank getFluidTank()
    {
        return this.fluidTank;
    }

    public SteamTank getSteamTank()
    {
        return this.steamTank;
    }
}
