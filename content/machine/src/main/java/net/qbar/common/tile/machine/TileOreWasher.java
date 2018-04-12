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
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.module.impl.*;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.machine.module.impl.AutomationModule;
import net.qbar.common.util.FluidUtils;

public class TileOreWasher extends TileTickingModularMachine implements IContainerProvider
{
    public TileOreWasher()
    {
        super(QBarMachines.ORE_WASHER);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new FluidStorageModule(this));
        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new CraftingInventoryModule(this));
        this.addModule(new CraftingModule(this));
        this.addModule(new AutomationModule(this));
        this.addModule(new IOModule(this));
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        CraftingInventoryModule inventory = this.getModule(CraftingInventoryModule.class);
        CraftingModule crafter = this.getModule(CraftingModule.class);
        SteamModule steamEngine = this.getModule(SteamModule.class);
        FluidStorageModule fluidStorage = this.getModule(FluidStorageModule.class);

        return new ContainerBuilder("orewasher", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(inventory)
                .recipeSlot(0, QBarRecipeHandler.ORE_WASHER_UID, 0, 47, 36,
                        slot -> inventory.isBufferEmpty() && inventory.isOutputEmpty())
                .outputSlot(1, 107, 35).outputSlot(2, 125, 35).displaySlot(3, -1000, 0)
                .syncFloatValue(crafter::getCurrentProgress, crafter::setCurrentProgress)
                .syncFloatValue(crafter::getMaxProgress, crafter::setMaxProgress)
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("washer"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("washer"))::setFluid)
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam).addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        IFluidHandler tank = this.getModule(FluidStorageModule.class).getFluidHandler("washer");

        if (FluidUtils.drainPlayerHand(tank, player) || FluidUtils.fillPlayerHand(tank, player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(QBarConstants.MODINSTANCE, MachineGui.OREWASHER.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }
}
