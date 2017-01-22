package net.qbar.common.tile;

import java.text.NumberFormat;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.fluid.DirectionalTank;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;

public class TileBoiler extends TileInventoryBase implements ITileInfoProvider, ITickable, IContainerProvider
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

    private int                   heat;
    private final int             maxHeat;

    private int                   currentBurnTime;
    private int                   maxBurnTime;

    public TileBoiler()
    {
        super("TileBoiler", 1);

        this.fluidTank = new DirectionalTank("TileBoiler", new FilteredFluidTank(Fluid.BUCKET_VOLUME * 4,
                stack -> stack != null && stack.getFluid() != null && stack.getFluid().equals(FluidRegistry.WATER)),
                new EnumFacing[0], new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST });
        this.steamTank = new SteamTank(0, 4000, SteamUtil.AMBIANT_PRESSURE * 2);

        this.maxHeat = 3000;
    }

    @Override
    public void update()
    {
        if (this.world.isRemote)
            return;
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

        if (this.maxBurnTime == 0 && !this.getStackInSlot(0).isEmpty())
        {
            this.maxBurnTime = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(0)) / 2;
            this.decrStackSize(0, 1);
        }
        if (this.currentBurnTime < this.maxBurnTime)
        {
            this.currentBurnTime++;
            this.heat++;
        }
        else
        {
            this.currentBurnTime = 0;
            this.maxBurnTime = 0;
        }

        if (this.heat >= 900)
        {
            int toProduce = (int) (1 / Math.E * (this.heat / 100));
            final FluidStack drained = this.fluidTank.getInternalFluidHandler().drain(toProduce, true);
            if (drained != null)
                toProduce = drained.amount;
            else
                toProduce = 0;
            this.steamTank.fillSteam(toProduce, true);
            if (toProduce != 0 && this.world.getTotalWorldTime() % 2 == 0)
                this.heat--;
        }

        if (this.world.getTotalWorldTime() % 5 == 0 && this.heat > 0)
            this.heat--;
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

        tag.setInteger("heat", this.heat);
        tag.setInteger("currentBurnTime", this.currentBurnTime);
        tag.setInteger("maxBurnTime", this.maxBurnTime);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.fluidTank.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));

        this.heat = tag.getInteger("heat");
        this.currentBurnTime = tag.getInteger("currentBurnTime");
        this.maxBurnTime = tag.getInteger("maxBurnTime");
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
        lines.add("Heat " + this.heat + " / " + this.maxHeat);
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + TileBoiler.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + TileBoiler.pressureFormat.format(this.steamTank.getMaxPressure()));
    }

    public DirectionalTank getFluidTank()
    {
        return this.fluidTank;
    }

    public FluidStack getFluid()
    {
        return this.fluidTank.getInternalFluidHandler().getTankProperties()[0].getContents();
    }

    public void setFluid(final FluidStack fluid)
    {
        this.fluidTank.setFluidStack(fluid);
    }

    public SteamTank getSteamTank()
    {
        return this.steamTank;
    }

    public int getHeat()
    {
        return this.heat;
    }

    public void setHeat(final int heat)
    {
        this.heat = heat;
    }

    public int getCurrentBurnTime()
    {
        return this.currentBurnTime;
    }

    public void setCurrentBurnTime(final int currentBurnTime)
    {
        this.currentBurnTime = currentBurnTime;
    }

    public int getMaxBurnTime()
    {
        return this.maxBurnTime;
    }

    public void setMaxBurnTime(final int maxBurnTime)
    {
        this.maxBurnTime = maxBurnTime;
    }

    public int getMaxHeat()
    {
        return this.maxHeat;
    }

    public int getSteamAmount()
    {
        return this.getSteamTank().getSteam();
    }

    public void setSteamAmount(final int amount)
    {
        this.getSteamTank().setSteam(amount);
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("boiler", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).slot(0, 80, 43).syncIntegerValue(this::getHeat, this::setHeat)
                .syncIntegerValue(this::getMaxBurnTime, this::setMaxBurnTime)
                .syncIntegerValue(this::getCurrentBurnTime, this::setCurrentBurnTime)
                .syncIntegerValue(this::getSteamAmount, this::setSteamAmount)
                .syncFluidValue(this::getFluid, this::setFluid).addInventory().create();
    }
}
