package net.ros.common.tile.machine;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.ROSConstants;
import net.ros.common.grid.IConnectionAware;
import net.ros.common.grid.impl.CableGrid;
import net.ros.common.init.ROSItems;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.steam.ISteamTank;
import net.ros.common.steam.LinkedSteamTank;
import net.ros.common.util.FluidUtils;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.machine.component.SteamComponent;
import net.ros.common.machine.module.impl.FluidStorageModule;

public class TileSteamTank extends TileModularMachine implements IContainerProvider, IConnectionAware
{
    @Getter
    private int tier;

    public TileSteamTank(int tier)
    {
        super(tier == 0 ? Machines.SMALL_STEAM_TANK : Machines.MEDIUM_STEAM_TANK);

        this.tier = tier;
    }

    public TileSteamTank()
    {
        this.tier = -1;
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 0));
        this.addModule(new FluidStorageModule(this)
                .addFilter("water", FluidUtils.WATER_FILTER));
        this.addModule(new SteamModule(this, this::createTank));

        this.addModule(new IOModule(this));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        this.tier = tag.getInteger("tier");

        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("tier", this.tier);

        return super.writeToNBT(tag);
    }

    private ISteamTank createTank(SteamComponent steamComponent)
    {
        return new LinkedSteamTank(0, steamComponent.getSteamCapacity(), steamComponent.getMaxPressureCapacity(),
                (IFluidTank) this.getModule(FluidStorageModule.class).getFluidHandler("water"));
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        SteamModule steamEngine = this.getModule(SteamModule.class);
        FluidStorageModule fluidStorage = this.getModule(FluidStorageModule.class);

        return new ContainerBuilder("steamtank", player)
                .player(player).inventory(8, 84).hotbar(8, 142).addInventory()
                .tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam)
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("water"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("water"))::setFluid)
                .addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        IFluidHandler water = this.getModule(FluidStorageModule.class).getFluidHandler("water");
        if (FluidUtils.drainPlayerHand(water, player) || FluidUtils.fillPlayerHand(water, player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(ROSConstants.MODINSTANCE, MachineGui.STEAMTANK.getUniqueID(), this.world, this.pos.getX(),
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
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }
}
