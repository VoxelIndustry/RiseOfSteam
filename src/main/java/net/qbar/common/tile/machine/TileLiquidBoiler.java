package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.gui.EGui;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.TileInventoryBase;
import net.qbar.common.util.FluidUtils;

import java.util.List;

public class TileLiquidBoiler extends TileInventoryBase implements ITickable, IContainerProvider, ITileMultiblockCore
{
    private final FluidTank waterTank, fuelTank;
    private final SteamTank steamTank;

    private int             heat;
    private final int       maxHeat;

    public TileLiquidBoiler()
    {
        super("liquidboiler", 0);
        waterTank = new FilteredFluidTank(Fluid.BUCKET_VOLUME * 64,
                fluidStack -> fluidStack.getFluid() == (FluidRegistry.WATER));
        fuelTank = new FilteredFluidTank(Fluid.BUCKET_VOLUME * 48,
                fluidStack -> fluidStack.getFluid() != (FluidRegistry.WATER));
        steamTank = new SteamTank(0, Fluid.BUCKET_VOLUME * 32, SteamUtil.AMBIANT_PRESSURE * 2);

        this.maxHeat = 3000;
    }

    @Override
    public void update()
    {

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

        tag.setInteger("heat", this.heat);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));

        if (tag.hasKey("waterTank"))
            this.waterTank.readFromNBT(tag);
        if (tag.hasKey("fuelTank"))
            this.fuelTank.readFromNBT(tag);

        this.heat = tag.getInteger("heat");
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
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        return null;
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("liquidboiler", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).syncIntegerValue(this::getHeat, this::setHeat)
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

    public int getHeat()
    {
        return this.heat;
    }

    public void setHeat(final int heat)
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
                return true;
        }
        else
        {
            if (FluidUtils.drainPlayerHand(this.getWaterTank(), player)
                    || FluidUtils.fillPlayerHand(this.getWaterTank(), player))
                return true;
        }
        player.openGui(QBar.instance, EGui.LIQUIDBOILER.ordinal(), this.getWorld(), this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }
}