package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.gui.EGui;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.recipe.LiquidBoilerRecipe;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamStack;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.TileInventoryBase;
import net.qbar.common.util.FluidUtils;

import java.util.List;
import java.util.Optional;

public class TileLiquidBoiler extends TileInventoryBase implements ITickable, IContainerProvider, ITileMultiblockCore
{
    private final FluidTank waterTank, fuelTank;
    private final SteamTank steamTank;

    private float           heat;
    private final int       maxHeat;

    public TileLiquidBoiler()
    {
        super("liquidboiler", 0);
        waterTank = new FilteredFluidTank(Fluid.BUCKET_VOLUME * 64,
                fluidStack -> fluidStack != null && fluidStack.getFluid() == (FluidRegistry.WATER));
        fuelTank = new FilteredFluidTank(Fluid.BUCKET_VOLUME * 48,
                fluidStack -> fluidStack != null && fluidStack.getFluid() != (FluidRegistry.WATER));
        steamTank = new SteamTank(0, Fluid.BUCKET_VOLUME * 32, SteamUtil.AMBIANT_PRESSURE * 2);

        this.maxHeat = 3000;
    }

    private Fluid              cachedFluid;
    private LiquidBoilerRecipe recipe;
    private double             pendingFuel = 0;

    @Override
    public void update()
    {
        if (this.isClient())
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
        }

        if (this.steamTank.getPressure() >= this.steamTank.getMaxPressure() && this.isServer())
            this.world.createExplosion(null, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 3, true);

        if (this.isServer())
        {
            if (this.getFuel() != null && this.getFuel().getFluid() != this.cachedFluid)
            {
                recipe = null;
                Optional<QBarRecipe> recipeSearched = QBarRecipeHandler.getRecipe(QBarRecipeHandler.LIQUIDBOILER_UID,
                        this.getFuel());

                if (recipeSearched.isPresent())
                    recipe = (LiquidBoilerRecipe) recipeSearched.get();
                this.cachedFluid = this.getFuel().getFluid();
            }

            if (recipe != null)
            {
                double toConsume = 1000.0 / recipe.getTime();
                this.pendingFuel += toConsume;

                toConsume = Math.min((int) pendingFuel, this.getFuelTank().getFluidAmount());

                this.heat += recipe.getRecipeOutputs(SteamStack.class).get(0).getRawIngredient().getAmount()
                        * toConsume;
                this.getFuelTank().drain((int) toConsume, true);
                this.pendingFuel -= toConsume;
            }

            if (this.heat >= 900)
            {
                int toProduce = (int) (1 / Math.E * (this.heat / 100));
                final FluidStack drained = this.getWaterTank().drain(toProduce, true);
                if (drained != null)
                    toProduce = drained.amount;
                else
                    toProduce = 0;
                this.steamTank.fillSteam(toProduce, true);
                if (toProduce != 0 && this.world.getTotalWorldTime() % 2 == 0)
                    this.heat--;
            }

            if (this.world.getTotalWorldTime() % 5 == 0)
            {
                if (this.heat > this.getMinimumTemp())
                    this.heat--;
                else if (this.heat < this.getMinimumTemp())
                    this.heat++;
            }
            this.sync();
        }
    }

    public int getMinimumTemp()
    {
        return (int) (this.world.getBiome(this.getPos()).getFloatTemperature(this.pos) * 200);
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
    public void addInfo(final List<String> lines)
    {
        if (this.getWaterTank().getFluid() != null)
        {
            lines.add("Containing " + this.getWaterTank().getFluid().getFluid().getName());
            lines.add(this.getWaterTank().getFluidAmount() + " / " + this.getWaterTank().getCapacity() + " mB");
        }
        if (this.getFuelTank().getFluid() != null)
        {
            lines.add("Containing " + this.getFuelTank().getFluid().getFluid().getName());
            lines.add(this.getFuelTank().getFluidAmount() + " / " + this.getFuelTank().getCapacity() + " mB");
        }
        lines.add("Heat " + this.heat + " / " + this.maxHeat);
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.steamTank.getMaxPressure()));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setTag("waterTank", this.waterTank.writeToNBT(new NBTTagCompound()));
        tag.setTag("fuelTank", this.fuelTank.writeToNBT(new NBTTagCompound()));

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
        if (tag.hasKey("fuelTank"))
            this.fuelTank.readFromNBT(tag.getCompoundTag("fuelTank"));

        this.heat = tag.getFloat("heat");
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.pos, false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        System.out.println("ORIGIN");
        return this.hasCapability(capability, BlockPos.ORIGIN, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        return this.getCapability(capability, BlockPos.ORIGIN, facing);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && from.getY() == 2
                && facing == EnumFacing.UP)
            return true;
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && from.getY() == 0
                && facing.getAxis().isHorizontal())
            return true;
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && from.getY() == 2
                && facing == EnumFacing.UP)
            return (T) this.getSteamTank();
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && from.getY() == 0)
        {
            MultiblockSide side = Multiblocks.LIQUID_FUEL_BOILER.worldSideToMultiblockSide(new MultiblockSide(from, facing),
                    this.getFacing());
            if (side.getFacing() == EnumFacing.NORTH)
            {
                if (side.getPos().equals(BlockPos.ORIGIN))
                    return (T) this.getWaterTank();
                else if (side.getPos().getX() == 1 && side.getPos().getZ() == 0)
                    return (T) this.getFuelTank();
            }
            else if (side.getFacing() == EnumFacing.WEST)
            {
                if (side.getPos().getX() == 0 && side.getPos().getZ() == 1)
                    return (T) this.getWaterTank();
                else if (side.getPos().equals(BlockPos.ORIGIN))
                    return (T) this.getFuelTank();
            }
            else if (side.getFacing() == EnumFacing.SOUTH)
            {
                if (side.getPos().getX() == 1 && side.getPos().getZ() == 1)
                    return (T) this.getWaterTank();
                else if (side.getPos().getX() == 0 && side.getPos().getZ() == 1)
                    return (T) this.getFuelTank();
            }
            else if (side.getFacing() == EnumFacing.EAST)
            {
                if (side.getPos().getX() == 1 && side.getPos().getZ() == 0)
                    return (T) this.getWaterTank();
                else if (side.getPos().getX() == 1 && side.getPos().getZ() == 1)
                    return (T) this.getFuelTank();
            }
        }
        return null;
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("liquidboiler", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).syncFloatValue(this::getHeat, this::setHeat)
                .syncIntegerValue(this::getSteamAmount, this::setSteamAmount)
                .syncFluidValue(this::getWater, this::setWater).syncFluidValue(this::getFuel, this::setFuel)
                .addInventory().create();
    }

    public int getSteamAmount()
    {
        return this.getSteamTank().getSteam();
    }

    public void setSteamAmount(final int amount)
    {
        this.getSteamTank().setSteam(amount);
    }

    public SteamTank getSteamTank()
    {
        return this.steamTank;
    }

    public FluidTank getWaterTank()
    {
        return this.waterTank;
    }

    public FluidStack getWater()
    {
        return this.waterTank.getFluid();
    }

    public void setWater(final FluidStack fluid)
    {
        this.waterTank.setFluid(fluid);
    }

    public FluidTank getFuelTank()
    {
        return this.fuelTank;
    }

    public FluidStack getFuel()
    {
        return this.fuelTank.getFluid();
    }

    public void setFuel(final FluidStack fluid)
    {
        this.fuelTank.setFluid(fluid);
    }

    public float getHeat()
    {
        return this.heat;
    }

    public void setHeat(final float heat)
    {
        this.heat = heat;
    }

    public int getMaxHeat()
    {
        return this.maxHeat;
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;

        if (from.getY() == 0)
        {
            if (FluidUtils.drainPlayerHand(this.getFuelTank(), player)
                    || FluidUtils.fillPlayerHand(this.getFuelTank(), player))
            {
                this.markDirty();
                return true;
            }
        }
        else
        {
            if (FluidUtils.drainPlayerHand(this.getWaterTank(), player)
                    || FluidUtils.fillPlayerHand(this.getWaterTank(), player))
            {
                this.markDirty();
                return true;
            }
        }
        player.openGui(QBar.instance, EGui.LIQUIDBOILER.ordinal(), this.getWorld(), this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
    }
}