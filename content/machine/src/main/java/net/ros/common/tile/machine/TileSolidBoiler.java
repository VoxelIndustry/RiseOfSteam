package net.ros.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.ROSConstants;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.steam.SteamUtil;
import net.ros.common.util.FluidUtils;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.impl.FluidStorageModule;
import net.ros.common.tile.module.SteamBoilerModule;

@Getter
@Setter
public class TileSolidBoiler extends TileTickingModularMachine implements IContainerProvider
{
    private int currentBurnTime;
    private int maxBurnTime;

    public TileSolidBoiler()
    {
        super(Machines.SOLID_BOILER);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 1));
        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new FluidStorageModule(this)
                .addFilter("water", FluidUtils.WATER_FILTER));

        this.addModule(SteamBoilerModule.builder()
                .machine(this)
                .maxHeat(300).waterTank("water")
                .build());

        this.addModule(new IOModule(this));
    }

    @Override
    public void update()
    {
        super.update();

        if (this.isClient())
            return;

        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");
        if (this.maxBurnTime == 0 && !inventory.getStackInSlot(0).isEmpty())
        {
            this.maxBurnTime = TileEntityFurnace.getItemBurnTime(inventory.getStackInSlot(0)) / 2;
            if (this.maxBurnTime != 0)
                inventory.extractItem(0, 1, false);
        }

        SteamBoilerModule boiler = this.getModule(SteamBoilerModule.class);

        if (this.currentBurnTime < this.maxBurnTime)
        {
            if (boiler.getCurrentHeat() < boiler.getMaxHeat())
            {
                this.currentBurnTime++;
                boiler.addHeat(0.1f);
            }
            else
                boiler.setCurrentHeat(boiler.getMaxHeat());
        }
        else
        {
            this.currentBurnTime = 0;
            this.maxBurnTime = 0;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("currentBurnTime", this.currentBurnTime);
        tag.setInteger("maxBurnTime", this.maxBurnTime);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.currentBurnTime = tag.getInteger("currentBurnTime");
        this.maxBurnTime = tag.getInteger("maxBurnTime");
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        SteamModule steamEngine = this.getModule(SteamModule.class);
        SteamBoilerModule boiler = this.getModule(SteamBoilerModule.class);
        FluidStorageModule fluidStorage = this.getModule(FluidStorageModule.class);

        return new ContainerBuilder("solidboiler", player).player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .fuelSlot(0, 80, 43)
                .syncIntegerValue(this::getMaxBurnTime, this::setMaxBurnTime)
                .syncIntegerValue(this::getCurrentBurnTime, this::setCurrentBurnTime)
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
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        IFluidHandler water = this.getModule(FluidStorageModule.class).getFluidHandler("water");
        if (FluidUtils.drainPlayerHand(water, player)
                || FluidUtils.fillPlayerHand(water, player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(ROSConstants.MODINSTANCE, MachineGui.BOILER.getUniqueID(), this.getWorld(),
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.askServerSync();
    }
}
