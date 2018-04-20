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
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.module.InventoryModule;
import net.qbar.common.machine.module.impl.AutomationModule;
import net.qbar.common.machine.module.impl.CraftingModule;
import net.qbar.common.machine.module.impl.IOModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.SteamUtil;

public class TileSteamFurnace extends TileTickingModularMachine implements IContainerProvider
{
    public TileSteamFurnace()
    {
        super(QBarMachines.FURNACE_MK1);
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        InventoryModule inventory = this.getModule(InventoryModule.class);
        CraftingModule crafter = this.getModule(CraftingModule.class);
        SteamModule steamEngine = this.getModule(SteamModule.class);

        return new ContainerBuilder("furnacemk1", player).player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(inventory.getInventory("crafting"))
                .recipeSlot(0, QBarRecipeHandler.FURNACE_UID, 0, 47, 36,
                        slot -> crafter.isBufferEmpty() && crafter.isOutputEmpty())
                .outputSlot(1, 116, 35).displaySlot(2, -1000, 0)
                .syncFloatValue(crafter::getCurrentProgress, crafter::setCurrentProgress)
                .syncFloatValue(crafter::getMaxProgress, crafter::setMaxProgress)
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam).addInventory().create();
    }

    @Override
    public boolean onRightClick(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.STEAMFURNACE.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new InventoryModule(this));
        this.addModule(new CraftingModule(this));
        this.addModule(new AutomationModule(this));
        this.addModule(new IOModule(this));
    }
}
