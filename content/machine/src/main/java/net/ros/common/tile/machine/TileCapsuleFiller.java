package net.ros.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.ros.common.ROSConstants;
import net.ros.common.gui.MachineGui;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.Machines;
import net.ros.common.machine.component.SteamComponent;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.steam.ISteamHandler;
import net.ros.common.steam.ISteamHandlerItem;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.steam.SteamUtil;
import net.voxelindustry.steamlayer.container.BuiltContainer;
import net.voxelindustry.steamlayer.container.ContainerBuilder;
import net.voxelindustry.steamlayer.container.IContainerProvider;

public class TileCapsuleFiller extends TileTickingModularMachine implements IContainerProvider
{
    public TileCapsuleFiller()
    {
        super(Machines.CAPSULE_FILLER);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 1));

        this.getModule(InventoryModule.class).getInventory("basic")
                .addSlotFilter(0, stack -> stack.hasCapability(SteamCapabilities.ITEM_STEAM_HANDLER, EnumFacing.NORTH));
        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new IOModule(this));
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");
        ISteamHandler steamHandler = this.getModule(SteamModule.class).getInternalSteamHandler();
        if (!inventory.getStackInSlot(0).isEmpty())
        {
            ISteamHandlerItem item = inventory.getStackInSlot(0).getCapability(
                    SteamCapabilities.ITEM_STEAM_HANDLER, EnumFacing.NORTH);

            if (item != null && steamHandler.getSteam() > 0 &&
                    item.getSteam() < item.getCapacity() * item.getMaxPressure())
                item.fillSteam(steamHandler.drainSteam(
                        this.getDescriptor().get(SteamComponent.class).getSteamConsumption(), true), true);
        }
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.askServerSync();
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        SteamModule steamEngine = this.getModule(SteamModule.class);

        return new ContainerBuilder("capsule_filler", player)
                .player(player).inventory(8, 84).hotbar(8, 142).addInventory()
                .tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .filterSlot(0, 80, 36, stack -> stack.hasCapability(SteamCapabilities.ITEM_STEAM_HANDLER, EnumFacing.UP))
                .addInventory()
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam)
                .create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.CAPSULE_FILLER.getUniqueID(), this.getWorld(),
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }
}
