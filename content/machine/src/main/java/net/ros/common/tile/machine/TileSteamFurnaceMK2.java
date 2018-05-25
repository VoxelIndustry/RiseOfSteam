package net.ros.common.tile.machine;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.ros.common.ROSConstants;
import net.ros.common.init.ROSItems;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.recipe.RecipeHandler;
import net.ros.common.steam.SteamUtil;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.machine.event.RecipeChangeEvent;
import net.ros.common.machine.module.impl.AutomationModule;
import net.ros.common.machine.module.impl.CraftingModule;
import net.ros.common.tile.module.SteamHeaterModule;

public class TileSteamFurnaceMK2 extends TileTickingModularMachine implements IContainerProvider
{
    @Getter
    private ItemStack cachedStack;

    public TileSteamFurnaceMK2()
    {
        super(Machines.FURNACE_MK2);

        this.cachedStack = ItemStack.EMPTY;
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

        this.addModule(SteamHeaterModule.builder()
                .maxHeat(2000).heatPerTick(0.2f).steamPerHeat(1)
                .machine(this)
                .build());
        crafter.setEfficiencySupplier(machine ->
                machine.getModule(SteamHeaterModule.class).getCurrentHeat() /
                        machine.getModule(SteamHeaterModule.class).getMaxHeat());
    }

    private void onRecipeChange(RecipeChangeEvent e)
    {
        if (this.getModule(CraftingModule.class).getCurrentRecipe() != null)
            this.cachedStack = this.getModule(CraftingModule.class)
                    .getCurrentRecipe().getRecipeOutputs(ItemStack.class).get(0).getRaw();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setTag("cachedStack", this.cachedStack.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.cachedStack = new ItemStack(tag.getCompoundTag("cachedStack"));
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        InventoryModule inventory = this.getModule(InventoryModule.class);
        CraftingModule crafter = this.getModule(CraftingModule.class);
        SteamModule steamEngine = this.getModule(SteamModule.class);
        SteamHeaterModule heater = this.getModule(SteamHeaterModule.class);

        return new ContainerBuilder("furnacemk2", player).player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(inventory.getInventory("crafting"))
                .recipeSlot(0, RecipeHandler.FURNACE_UID, 0, 47, 36,
                        slot -> crafter.isBufferEmpty() && crafter.isOutputEmpty())
                .outputSlot(2, 116, 35).displaySlot(1, -1000, 0)
                .syncFloatValue(crafter::getCurrentProgress, crafter::setCurrentProgress)
                .syncFloatValue(crafter::getMaxProgress, crafter::setMaxProgress)
                .syncFloatValue(heater::getCurrentHeat, heater::setCurrentHeat)
                .syncFloatValue(heater::getMaxHeat, heater::setMaxHeat)
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam).addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.STEAMFURNACEMK2.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }
}
