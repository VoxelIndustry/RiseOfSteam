package net.qbar.common.tile.machine;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.fluid.DirectionalTank;
import net.qbar.common.grid.CableGrid;
import net.qbar.common.grid.IConnectionAware;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.TileInventoryBase;
import net.qbar.common.util.FluidUtils;

import javax.annotation.Nullable;
import java.util.List;

@Getter
public class TileSteamTank extends TileInventoryBase implements ITileMultiblockCore, IContainerProvider,
        IConnectionAware
{
    private int tier;

    private DirectionalTank fluidTank;
    private SteamTank       steamTank;

    public TileSteamTank(int tier)
    {
        super("steamtank", 0);

        this.tier = tier;

        int capacity = tier == 0 ? 64 : 128;

        this.fluidTank = new DirectionalTank("steamtank", new FluidTank(Fluid.BUCKET_VOLUME * capacity),
                new EnumFacing[0], new EnumFacing[]{EnumFacing.UP});
        this.steamTank = new LinkedSteamTank(0, Fluid.BUCKET_VOLUME * capacity,
                tier == 0 ? SteamUtil.BASE_PRESSURE * 1.5f : SteamUtil.BASE_PRESSURE * 2.5f,
                this.fluidTank.getInternalFluidHandler());
    }

    public TileSteamTank()
    {
        this(0);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.steamTank.getMaxPressure()));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setTag("fluid", this.fluidTank.writeToNBT(new NBTTagCompound()));
        tag.setTag("steam", this.steamTank.writeToNBT(new NBTTagCompound()));

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.fluidTank.readFromNBT(tag.getCompoundTag("fluid"));
        this.steamTank.readFromNBT(tag.getCompoundTag("steam"));
    }

    public FluidStack getFluid()
    {
        return this.fluidTank.getInternalFluidHandler().getFluid();
    }

    public void setFluid(final FluidStack fluid)
    {
        this.fluidTank.setFluidStack(fluid);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("steamtank", player)
                .player(player.inventory).inventory(8, 84).hotbar(8, 142).addInventory()
                .tile(this)
                .syncFluidValue(this::getFluid, this::setFluid)
                .syncIntegerValue(this.getSteamTank()::getSteam, this.getSteamTank()::setSteam)
                .addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        if (FluidUtils.drainPlayerHand(this.getFluidTank().getInternalFluidHandler(), player) ||
                FluidUtils.fillPlayerHand(this.getFluidTank().getInternalFluidHandler(), player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(QBarConstants.MODINSTANCE, MachineGui.STEAMTANK.getUniqueID(), this.world, this.pos.getX(),
                this.pos.getY(), this.pos.getZ());
        return false;
    }

    @Override
    public void connectTrigger(EnumFacing facing, CableGrid grid)
    {

    }

    @Override
    public void disconnectTrigger(EnumFacing facing, CableGrid grid)
    {

    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
                capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(
                    this.getFluidTank().getFluidHandler(EnumFacing.UP));
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY.cast(this.getSteamTank());
        return null;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
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

    private class LinkedSteamTank extends SteamTank
    {
        private IFluidTank fluidTank;

        private LinkedSteamTank(int steamAmount, int capacity, float maxPressure, IFluidTank fluidTank)
        {
            super(steamAmount, capacity, maxPressure);

            this.fluidTank = fluidTank;
        }

        @Override
        public int getCapacity()
        {
            return fluidTank.getFluidAmount();
        }
    }
}
