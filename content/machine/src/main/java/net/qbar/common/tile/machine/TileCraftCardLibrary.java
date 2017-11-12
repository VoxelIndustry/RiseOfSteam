package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.tile.TileMultiblockInventoryBase;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class TileCraftCardLibrary extends TileMultiblockInventoryBase
{
    public TileCraftCardLibrary()
    {
        super("craftcardlibrary", 72);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return IntStream.range(0, 72).toArray();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index >= 0 && index <= 71;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index >= 0 && index <= 71;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        Predicate<ItemStack> cardFilter = stack -> stack.getItem() == QBarItems.PUNCHED_CARD &&
                stack.hasTagCompound() && PunchedCardDataManager.getInstance().readFromNBT(stack.getTagCompound())
                .getID() == PunchedCardDataManager.ECardType.CRAFT.getID();

        return new ContainerBuilder("craftcardlibrary", player).player(player.inventory)
                .inventory(8, 123).hotbar(8, 181).addInventory()
                .tile(this)
                .filterSlotLine(0, 8, -25, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(9, 8, -7, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(18, 8, 11, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(27, 8, 29, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(36, 8, 47, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(45, 8, 65, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(54, 8, 83, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(63, 8, 101, 9, EnumFacing.Axis.X, cardFilter)
                .addInventory().create();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return this.hasCapability(capability, BlockPos.ORIGIN, facing) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        T rtn = this.getCapability(capability, BlockPos.ORIGIN, facing);
        return rtn != null ? rtn : super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) this.getInventoryWrapper(facing);
        return null;
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.CRAFTCARDLIBRARY.getUniqueID(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }
}
