package net.qbar.common.tile.machine;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.grid.IConnectionAware;
import net.qbar.common.grid.impl.CableGrid;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.component.SteamComponent;
import net.qbar.common.machine.module.impl.BasicInventoryModule;
import net.qbar.common.machine.module.impl.FluidStorageModule;
import net.qbar.common.machine.module.impl.IOModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.steam.ISteamTank;
import net.qbar.common.steam.LinkedSteamTank;
import net.qbar.common.util.FluidUtils;

public class TileSteamTank extends TileModularMachine implements IContainerProvider, IConnectionAware
{
    @Getter
    private int tier;

    public TileSteamTank(int tier)
    {
        super(tier == 0 ? QBarMachines.SMALL_STEAM_TANK : QBarMachines.MEDIUM_STEAM_TANK);

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

        this.addModule(new BasicInventoryModule(this, 0));
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
                .player(player.inventory).inventory(8, 84).hotbar(8, 142).addInventory()
                .tile(this.getModule(BasicInventoryModule.class))
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
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        IFluidHandler water = this.getModule(FluidStorageModule.class).getFluidHandler("water");
        if (FluidUtils.drainPlayerHand(water, player) || FluidUtils.fillPlayerHand(water, player))
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
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }
}
