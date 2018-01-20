package net.qbar.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.qbar.client.ParticleHelper;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.TileMultiblockInventoryBase;

import java.util.List;

@Getter
public abstract class TileBoilerBase extends TileMultiblockInventoryBase implements ITickable
{
    private final FluidTank waterTank;
    private final SteamTank steamTank;

    @Setter
    protected     float heat;
    private final float maxHeat;

    public TileBoilerBase(String name, int inventorySize, int maxHeat, int steamCapacity, int steamMaxPressure,
                          int waterCapacity)
    {
        super(name, inventorySize);

        this.waterTank = new FilteredFluidTank(waterCapacity,
                stack -> stack != null && stack.getFluid() != null && stack.getFluid().equals(FluidRegistry.WATER));
        this.steamTank = new SteamTank(0, steamCapacity, steamMaxPressure);

        this.maxHeat = maxHeat;
    }

    @Override
    public void update()
    {
        if (this.isClient())
        {
            if (this.steamTank.getPressure() / this.steamTank.getMaxPressure() >= 0.8f)
            {
                ParticleHelper.spawnParticles(EnumParticleTypes.SMOKE_LARGE, this.world, this.getPos());
                ParticleHelper.spawnParticles(EnumParticleTypes.FLAME, this.world, this.getPos());
            }
            else if (this.steamTank.getPressure() / this.steamTank.getMaxPressure() >= 0.65f)
                ParticleHelper.spawnParticles(EnumParticleTypes.SMOKE_NORMAL, this.world, this.getPos());
            if (this.steamTank.getPressure() / this.steamTank.getMaxPressure() >= 0.9f)
                ParticleHelper.spawnParticles(EnumParticleTypes.LAVA, this.world, this.getPos());
        }
        else if (this.steamTank.getPressure() >= this.steamTank.getMaxPressure())
            this.world.createExplosion(null, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 3, true);
    }

    public int getMinimumTemp()
    {
        return (int) (this.world.getBiome(this.getPos()).getTemperature(this.pos) * 20);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setTag("waterTank", this.waterTank.writeToNBT(new NBTTagCompound()));

        final NBTTagCompound subTag = new NBTTagCompound();
        this.steamTank.writeToNBT(subTag);
        tag.setTag("steamTank", subTag);

        tag.setFloat("heat", this.heat);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));

        if (tag.hasKey("waterTank"))
            this.waterTank.readFromNBT(tag.getCompoundTag("waterTank"));

        this.heat = tag.getFloat("heat");
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        if (this.waterTank != null && this.waterTank.getFluid() != null)
        {
            lines.add("Containing " + this.waterTank.getFluid().getFluid().getName());
            lines.add(this.waterTank.getFluidAmount() + " / " + this.waterTank.getCapacity() + " mB");
        }
        lines.add("Heat " + this.heat + " / " + this.maxHeat);
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.steamTank.getMaxPressure()));
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        return this.hasCapability(capability, BlockPos.ORIGIN, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        return this.getCapability(capability, BlockPos.ORIGIN, facing);
    }

    public FluidStack getWater()
    {
        return this.waterTank.getFluid();
    }

    public void setWater(final FluidStack fluid)
    {
        this.waterTank.setFluid(fluid);
    }

    public int getSteamAmount()
    {
        return this.getSteamTank().getSteam();
    }

    public void setSteamAmount(final int amount)
    {
        this.getSteamTank().setSteam(amount);
    }
}
