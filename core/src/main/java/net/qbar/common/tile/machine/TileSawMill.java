package net.qbar.common.tile.machine;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.grid.IBelt;
import net.qbar.common.gui.EGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.tile.TileCraftingMachineBase;

public class TileSawMill extends TileCraftingMachineBase
{
    public TileSawMill()
    {
        super(QBarMachines.SAW_MILL);
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;
        super.update();

        final EnumFacing orientation = this.getFacing().getOpposite();

        if (!this.isOutputEmpty() && this.hasBelt(orientation))
        {
            if (this.canInsert(this.getStackInSlot(this.getDescriptor().getOutputs()[0]), orientation))
            {
                this.insert(this.getInventoryWrapper(EnumFacing.DOWN).extractItem(0, 1, false), orientation);
                this.sync();
            }
        }
    }

    private void insert(final ItemStack stack, final EnumFacing facing)
    {
        ((IBelt) this.world.getTileEntity(this.pos.offset(facing, 2).down())).insert(stack, true);
    }

    private boolean canInsert(final ItemStack stack, final EnumFacing facing)
    {
        final IBelt belt = (IBelt) this.world.getTileEntity(this.pos.offset(facing, 2).down());

        return belt.insert(stack, false);
    }

    private boolean hasBelt(final EnumFacing facing)
    {
        final TileEntity tile = this.world.getTileEntity(this.pos.offset(facing, 2).down());

        return tile != null && tile instanceof IBelt;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        final EnumFacing orientation = this.getFacing();

        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && from == BlockPos.ORIGIN
                && facing == orientation.rotateY().getOpposite())
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

        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && from == BlockPos.ORIGIN
                && facing == orientation.rotateY().getOpposite())
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

        player.openGui(QBar.instance, EGui.SAWMILL.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction)
    {
        if (ArrayUtils.contains(this.getDescriptor().getInputs(), index) && this.isInputEmpty() && this.isBufferEmpty()
                && this.isOutputEmpty())
            return this.isItemValidForSlot(index, itemStackIn);
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
    }
}
