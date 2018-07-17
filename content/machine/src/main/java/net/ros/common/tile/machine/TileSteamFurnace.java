package net.ros.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.ros.common.ROSConstants;
import net.ros.common.init.ROSItems;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.recipe.RecipeHandler;
import net.ros.common.steam.SteamUtil;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.impl.AutomationModule;
import net.ros.common.machine.module.impl.CraftingModule;

public class TileSteamFurnace extends TileTickingModularMachine implements IContainerProvider
{
    public TileSteamFurnace()
    {
        super(Machines.FURNACE_MK1);
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        InventoryModule inventory = this.getModule(InventoryModule.class);
        CraftingModule crafter = this.getModule(CraftingModule.class);
        SteamModule steamEngine = this.getModule(SteamModule.class);

        return new ContainerBuilder("furnacemk1", player).player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(inventory.getInventory("crafting"))
                .recipeSlot(0, RecipeHandler.FURNACE_UID, 0, 47, 36,
                        slot -> crafter.isBufferEmpty() && crafter.isOutputEmpty())
                .outputSlot(2, 116, 35).displaySlot(1, -1000, 0)
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
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.STEAM_FURNACE.getUniqueID(), this.world,
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
