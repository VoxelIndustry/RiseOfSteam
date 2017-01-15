package net.qbar.common.tile;

import java.text.NumberFormat;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.common.fluid.DirectionalTank;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;

public class TileBoiler extends QBarTileBase implements ITileInfoProvider, ITickable
{
    private static NumberFormat pressureFormat;

    static
    {
        TileBoiler.pressureFormat = NumberFormat.getInstance();
        TileBoiler.pressureFormat.setMaximumFractionDigits(2);
        TileBoiler.pressureFormat.setMinimumFractionDigits(2);
    }

    private final DirectionalTank fluidTank;
    private final SteamTank       steamTank;

    public TileBoiler()
    {
        this.fluidTank = new DirectionalTank("TileBoiler",
                new FilteredFluidTank(Fluid.BUCKET_VOLUME * 4,
                        stack -> stack.getFluid() != null && stack.getFluid().equals(FluidRegistry.WATER)),
                new EnumFacing[0], new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST });
        this.steamTank = new SteamTank(0, 4000, SteamUtil.AMBIANT_PRESSURE * 2);
    }

    @Override
    public void update()
    {

        if (this.steamTank.getPressure() / this.steamTank.getMaxPressure() >= 0.8f)
        {
            this.spawnParticles(EnumParticleTypes.SMOKE_LARGE);
            this.spawnParticles(EnumParticleTypes.FLAME);
        }
        else if (this.steamTank.getPressure() / this.steamTank.getMaxPressure() >= 0.65f)
            this.spawnParticles(EnumParticleTypes.SMOKE_NORMAL);
        if (this.steamTank.getPressure() / this.steamTank.getMaxPressure() >= 0.9f)
            this.spawnParticles(EnumParticleTypes.LAVA);

        if (this.steamTank.getPressure() >= this.steamTank.getMaxPressure())
            this.world.createExplosion(null, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 3, true);

        this.steamTank.fillSteam(10, true);
    }

    private void spawnParticles(final EnumParticleTypes particle)
    {
        final int rand = this.world.rand.nextInt(5);

        switch (rand)
        {
            case 0:
                this.world.spawnParticle(particle, this.pos.getX(),
                        this.pos.getY() + this.world.rand.nextFloat() / 2 + 0.2,
                        this.pos.getZ() + .25 + this.world.rand.nextFloat() / 2, -0.01, 0.1f, 0);
                this.world.spawnParticle(particle, this.pos.getX(),
                        this.pos.getY() + this.world.rand.nextFloat() / 2 + 0.2,
                        this.pos.getZ() + .25 + this.world.rand.nextFloat() / 2, -0.01, 0.1f, 0);
                break;
            case 1:
                this.world.spawnParticle(particle, this.pos.getX() + .25 + this.world.rand.nextFloat() / 2,
                        this.pos.getY() + this.world.rand.nextFloat() / 2 + 0.2, this.pos.getZ(), 0, 0.1f, -0.01);
                this.world.spawnParticle(particle, this.pos.getX() + .25 + this.world.rand.nextFloat() / 2,
                        this.pos.getY() + this.world.rand.nextFloat() / 2 + 0.2, this.pos.getZ(), 0, 0.1f, -0.01);
                break;
            case 2:
                this.world.spawnParticle(particle, this.pos.getX() + 1,
                        this.pos.getY() + this.world.rand.nextFloat() / 2 + 0.2,
                        this.pos.getZ() + .25 + this.world.rand.nextFloat() / 2, 0.01, 0.1f, 0);
                this.world.spawnParticle(particle, this.pos.getX() + 1,
                        this.pos.getY() + this.world.rand.nextFloat() / 2 + 0.2,
                        this.pos.getZ() + .25 + this.world.rand.nextFloat() / 2, 0.01, 0.1f, 0);
                break;
            case 3:
                this.world.spawnParticle(particle, this.pos.getX() + .25 + this.world.rand.nextFloat() / 2,
                        this.pos.getY() + this.world.rand.nextFloat() / 2 + 0.2, this.pos.getZ() + 1, 0, 0.1f, 0.01);
                this.world.spawnParticle(particle, this.pos.getX() + .25 + this.world.rand.nextFloat() / 2,
                        this.pos.getY() + this.world.rand.nextFloat() / 2 + 0.2, this.pos.getZ() + 1, 0, 0.1f, 0.01);
                break;
            case 4:
                this.world.spawnParticle(particle, this.pos.getX() + .25 + this.world.rand.nextFloat() / 2,
                        this.pos.getY() + 1, this.pos.getZ() + this.world.rand.nextFloat() / 2, 0.01, 0.1f, 0.01);
                this.world.spawnParticle(particle, this.pos.getX() + .25 + this.world.rand.nextFloat() / 2,
                        this.pos.getY() + 1, this.pos.getZ() + this.world.rand.nextFloat() / 2, 0.01, 0.1f, 0.01);
                break;
            default:
                break;
        }
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
        if (this.fluidTank.getInternalFluidHandler() != null
                && this.fluidTank.getInternalFluidHandler().getTankProperties()[0].getContents() != null)
        {
            lines.add("Containing " + this.fluidTank.getInternalFluidHandler().getTankProperties()[0].getContents()
                    .getFluid().getName());
            lines.add(this.fluidTank.getInternalFluidHandler().getTankProperties()[0].getContents().amount + " / "
                    + this.fluidTank.getInternalFluidHandler().getTankProperties()[0].getCapacity() + " mB");
        }
        lines.add("Steam " + this.steamTank.getAmount() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + TileBoiler.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + TileBoiler.pressureFormat.format(this.steamTank.getMaxPressure()));
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
