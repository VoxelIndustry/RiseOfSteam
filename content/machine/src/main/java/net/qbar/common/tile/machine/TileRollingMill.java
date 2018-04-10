package net.qbar.common.tile.machine;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.event.RecipeChangeEvent;
import net.qbar.common.machine.module.impl.CraftingInventoryModule;
import net.qbar.common.machine.module.impl.CraftingModule;
import net.qbar.common.machine.module.impl.IOModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.AutomationModule;

public class TileRollingMill extends TileTickingModularMachine implements IContainerProvider
{
    @Getter
    private ItemStack cachedStack;

    public TileRollingMill()
    {
        super(QBarMachines.ROLLING_MILL);

        this.cachedStack = ItemStack.EMPTY;
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        CraftingModule crafter = new CraftingModule(this);
        crafter.setOnRecipeChange(this::onRecipeChange);

        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new CraftingInventoryModule(this));
        this.addModule(crafter);
        this.addModule(new AutomationModule(this));
        this.addModule(new IOModule(this));
    }

    @Override
    public boolean hasFastRenderer()
    {
        return true;
    }

    public void onRecipeChange(RecipeChangeEvent e)
    {
        if (this.getModule(CraftingModule.class).getCurrentRecipe() != null)
            this.cachedStack = this.getModule(CraftingModule.class).getCurrentRecipe()
                    .getRecipeOutputs(ItemStack.class).get(0).getRawIngredient();
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
        CraftingInventoryModule inventory = this.getModule(CraftingInventoryModule.class);
        CraftingModule crafter = this.getModule(CraftingModule.class);
        SteamModule steamEngine = this.getModule(SteamModule.class);

        return new ContainerBuilder("furnacemk1", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(inventory)
                .recipeSlot(0, QBarRecipeHandler.ROLLINGMILL_UID, 0, 47, 36,
                        slot -> inventory.isBufferEmpty() && inventory.isOutputEmpty())
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

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.ROLLINGMILL.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return ((BlockMultiblockBase) this.getBlockType()).getMultiblock().getBox(this.getFacing()).offset(this.pos);
    }
}
