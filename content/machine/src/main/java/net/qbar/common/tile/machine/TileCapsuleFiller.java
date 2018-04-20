package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.inventory.InventoryHandler;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.component.SteamComponent;
import net.qbar.common.machine.module.InventoryModule;
import net.qbar.common.machine.module.impl.IOModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.steam.ISteamHandlerItem;
import net.qbar.common.steam.SteamCapabilities;
import net.qbar.common.steam.SteamUtil;

public class TileCapsuleFiller extends TileTickingModularMachine implements IContainerProvider
{
    public TileCapsuleFiller()
    {
        super(QBarMachines.CAPSULE_FILLER);
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
            this.forceSync();
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        SteamModule steamEngine = this.getModule(SteamModule.class);

        return new ContainerBuilder("capsule_filler", player)
                .player(player).inventory(8, 84).hotbar(8, 142).addInventory()
                .tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .steamSlot(0, 80, 36)
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam)
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

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.CAPSULE_FILLER.getUniqueID(), this.getWorld(),
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }
}
