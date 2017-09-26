package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.qbar.QBar;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.EGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.tile.TileMultiblockInventoryBase;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class TileCraftCardLibrary extends TileMultiblockInventoryBase
{
    public TileCraftCardLibrary()
    {
        super("craftcardlibrary", 64);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return IntStream.range(0, 64).toArray();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index >= 0 && index <= 63;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index >= 0 && index <= 63;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        Predicate<ItemStack> cardFilter = stack -> stack.getItem() == QBarItems.PUNCHED_CARD &&
                stack.hasTagCompound() && PunchedCardDataManager.getInstance().readFromNBT(stack.getTagCompound())
                .getID() == PunchedCardDataManager.ECardType.CRAFT.getID();

        return new ContainerBuilder("craftcardlibrary", player).player(player.inventory)
                .inventory(8, 162).hotbar(8, 220).addInventory()
                .tile(this)
                .filterSlotLine(0, 8, 7, 8, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(7, 8, 25, 8, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(15, 8, 43, 8, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(23, 8, 61, 8, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(31, 8, 79, 8, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(39, 8, 97, 8, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(47, 8, 115, 8, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(55, 8, 133, 8, EnumFacing.Axis.X, cardFilter)
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

        player.openGui(QBar.instance, EGui.CRAFTCARDLIBRARY.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }
}
