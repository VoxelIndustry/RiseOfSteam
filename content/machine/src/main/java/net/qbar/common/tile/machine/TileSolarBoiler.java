package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.event.TickHandler;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.module.impl.BasicInventoryModule;
import net.qbar.common.machine.module.impl.FluidStorageModule;
import net.qbar.common.machine.module.impl.IOModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.multiblock.ITileMultiblock;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.ILoadable;
import net.qbar.common.tile.module.SteamBoilerModule;
import net.qbar.common.util.FluidUtils;

import java.util.EnumMap;

public class TileSolarBoiler extends TileTickingModularMachine implements ILoadable, IContainerProvider
{
    private EnumMap<EnumFacing, TileSolarMirror> mirrors;

    public TileSolarBoiler()
    {
        super(QBarMachines.SOLAR_BOILER);

        this.mirrors = new EnumMap<>(EnumFacing.class);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new BasicInventoryModule(this, 0));
        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new FluidStorageModule(this)
                .addFilter("water", FluidUtils.WATER_FILTER));

        this.addModule(SteamBoilerModule.builder()
                .machine(this)
                .maxHeat(400).waterTank("water")
                .build());

        this.addModule(new IOModule(this));
    }

    @Override
    public void update()
    {
        super.update();

        if (this.isClient())
            return;
        float sunValue = getSunValue();

        int totalMirrorCount = this.mirrors.values().stream().mapToInt(TileSolarMirror::getMirrorCount).sum();
        float producedHeat = (0.001f * totalMirrorCount) * sunValue;

        SteamBoilerModule boiler = this.getModule(SteamBoilerModule.class);
        if (boiler.getCurrentHeat() < boiler.getMaxHeat())
        {
            if (boiler.getCurrentHeat() + producedHeat < boiler.getMaxHeat())
                boiler.addHeat(producedHeat);
            else
                boiler.setCurrentHeat(boiler.getMaxHeat());
        }
    }

    private float getSunValue()
    {
        float baseValue = 1;
        if (this.world.isRaining())
            baseValue -= 0.4f;
        if (this.world.isThundering())
            baseValue -= 0.3f;
        if (!this.world.isDaytime())
            baseValue = 0;
        return baseValue;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        SteamModule steamEngine = this.getModule(SteamModule.class);
        SteamBoilerModule boiler = this.getModule(SteamBoilerModule.class);
        FluidStorageModule fluidStorage = this.getModule(FluidStorageModule.class);

        return new ContainerBuilder("solarboiler", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this.getModule(BasicInventoryModule.class))
                .syncFloatValue(boiler::getCurrentHeat, boiler::setCurrentHeat)
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam)
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("water"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("water"))::setFluid)
                .addInventory().create();
    }

    @Override
    public boolean onRightClick(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        IFluidHandler water = this.getModule(FluidStorageModule.class).getFluidHandler("water");
        if (FluidUtils.drainPlayerHand(water, player)
                || FluidUtils.fillPlayerHand(water, player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(QBarConstants.MODINSTANCE, MachineGui.SOLARBOILER.getUniqueID(), this.getWorld(),
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
        else
            TickHandler.loadables.add(this);
    }

    @Override
    public void load()
    {
        this.checkMirrors();
    }

    public void checkMirrors()
    {
        this.mirrors.clear();
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos search = this.getPos().offset(facing, 2).up(3);
            if (this.world.getBlockState(search).getBlock() == QBarBlocks.SOLAR_MIRROR)
                this.mirrors.put(facing,
                        (TileSolarMirror) ((ITileMultiblock) this.world.getTileEntity(search)).getCore());
        }
    }
}
