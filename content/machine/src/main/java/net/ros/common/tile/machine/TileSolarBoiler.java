package net.ros.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.ROSConstants;
import net.ros.common.event.TickHandler;
import net.ros.common.gui.MachineGui;
import net.ros.common.init.ROSBlocks;
import net.ros.common.init.ROSItems;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.FluidStorageModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.multiblock.ITileMultiblock;
import net.ros.common.steam.SteamUtil;
import net.ros.common.tile.ILoadable;
import net.ros.common.tile.module.SteamBoilerModule;
import net.ros.common.util.FluidUtils;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.container.IContainerProvider;

import java.util.EnumMap;

public class TileSolarBoiler extends TileTickingModularMachine implements ILoadable, IContainerProvider
{
    private EnumMap<EnumFacing, TileSolarMirror> mirrors;

    public TileSolarBoiler()
    {
        super(Machines.SOLAR_BOILER);

        this.mirrors = new EnumMap<>(EnumFacing.class);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 0));
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

        return new ContainerBuilder("solarboiler", player).player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .addInventory()
                .syncFloatValue(boiler::getCurrentHeat, boiler::setCurrentHeat)
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam)
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("water"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("water"))::setFluid)
                .create();
    }

    @Override
    public boolean onRightClick(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        IFluidHandler water = this.getModule(FluidStorageModule.class).getFluidHandler("water");
        if (FluidUtils.drainPlayerHand(water, player)
                || FluidUtils.fillPlayerHand(water, player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(ROSConstants.MODINSTANCE, MachineGui.SOLAR_BOILER.getUniqueID(), this.getWorld(),
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.askServerSync();
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
            if (this.world.getBlockState(search).getBlock() == ROSBlocks.SOLAR_MIRROR)
                this.mirrors.put(facing,
                        (TileSolarMirror) ((ITileMultiblock) this.world.getTileEntity(search)).getCore());
        }
    }
}
