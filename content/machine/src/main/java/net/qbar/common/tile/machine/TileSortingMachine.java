package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import net.qbar.common.machine.event.RecipeChangeEvent;
import net.qbar.common.machine.module.InventoryModule;
import net.qbar.common.machine.module.impl.AutomationModule;
import net.qbar.common.machine.module.impl.CraftingModule;
import net.qbar.common.machine.module.impl.IOModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.SteamUtil;

public class TileSortingMachine extends TileTickingModularMachine implements IContainerProvider
{
    public TileSortingMachine()
    {
        super(QBarMachines.SORTING_MACHINE);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new InventoryModule(this));

        CraftingModule crafter = new CraftingModule(this);
        crafter.setOnRecipeChange(this::onRecipeChange);
        this.addModule(crafter);
        this.addModule(new AutomationModule(this));
        this.addModule(new IOModule(this));
    }

    private void onRecipeChange(RecipeChangeEvent e)
    {
        if (e.getNextRecipe() != null && e.getNextRecipe().getRecipeOutputs(ItemStack.class).size() > 4)
            e.setCancelled(true);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("crafting");
        CraftingModule crafter = this.getModule(CraftingModule.class);
        SteamModule steamEngine = this.getModule(SteamModule.class);

        return new ContainerBuilder("sortingmachine", player).player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(inventory)
                .recipeSlot(0, QBarRecipeHandler.SORTING_MACHINE_UID, 0, 47, 36,
                        slot -> crafter.isBufferEmpty() && crafter.isOutputEmpty())
                .outputSlot(1, 107, 26).outputSlot(2, 125, 26).outputSlot(3, 107, 44).outputSlot(4, 125, 44)
                .displaySlot(2, -1000, 0)
                .syncFloatValue(crafter::getCurrentProgress, crafter::setCurrentProgress)
                .syncFloatValue(crafter::getMaxProgress, crafter::setMaxProgress)
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

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.SORTINGMACHINE.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }
}
