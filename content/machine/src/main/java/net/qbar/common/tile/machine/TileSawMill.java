package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.SteamCapabilities;
import net.qbar.common.tile.TileCraftingMachineBase;
import org.apache.commons.lang3.ArrayUtils;

public class TileSawMill extends TileCraftingMachineBase
{
    private ItemStack cachedStack;

    public TileSawMill()
    {
        super(QBarMachines.SAW_MILL);

        this.cachedStack = ItemStack.EMPTY;
    }

    @Override
    public void onRecipeChange()
    {
        if (this.getCurrentRecipe() != null)
            this.cachedStack = this.getCurrentRecipe().getRecipeOutputs(ItemStack.class).get(0).getRawIngredient();
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
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        final EnumFacing orientation = this.getFacing();

        if (capability == SteamCapabilities.STEAM_HANDLER &&
                (facing == EnumFacing.DOWN || (from == BlockPos.ORIGIN
                        && (facing == orientation.rotateY().getOpposite() || facing == orientation.rotateY()))))
            return true;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && from != BlockPos.ORIGIN
                && facing.getAxis() == orientation.getAxis())
            return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        final EnumFacing orientation = this.getFacing();

        if (capability == SteamCapabilities.STEAM_HANDLER &&
                (facing == EnumFacing.DOWN || (from == BlockPos.ORIGIN
                        && (facing == orientation.rotateY().getOpposite() || facing == orientation.rotateY()))))
            return (T) this.getSteamTank();
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && from != BlockPos.ORIGIN
                && facing.getAxis() == orientation.getAxis())
        {
            return (T) this.getInventoryWrapper(facing);
        }
        return null;
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("sawmill", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this)
                .recipeSlot(0, QBarRecipeHandler.SAW_MILL_UID, 0, 47, 36,
                        slot -> this.isBufferEmpty() && this.isOutputEmpty())
                .outputSlot(1, 116, 35).displaySlot(2, -1000, 0)
                .syncFloatValue(this::getCurrentProgress, this::setCurrentProgress)
                .syncFloatValue(this::getMaxProgress, this::setMaxProgress)
                .syncIntegerValue(this.getSteamTank()::getSteam, this.getSteamTank()::setSteam).addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.SAWMILL.getUniqueID(), this.world, this.pos.getX(), this
                        .pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction)
    {
        if (ArrayUtils.contains(this.getCrafter().getInputs(), index) && this.isInputEmpty() && this.isBufferEmpty()
                && this.isOutputEmpty())
            return this.isItemValidForSlot(index, itemStackIn);
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public boolean hasFastRenderer()
    {
        return true;
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
    }

    public ItemStack getCachedStack()
    {
        return this.cachedStack;
    }
}
