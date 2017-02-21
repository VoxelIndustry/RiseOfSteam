package net.qbar.common.container.slot;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotFuel extends ListenerSlot
{
    public SlotFuel(final IInventory inventory, final int index, final int x, final int y)
    {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(final ItemStack stack)
    {
        return TileEntityFurnace.isItemFuel(stack) || SlotFuel.isBucket(stack);
    }

    @Override
    public int getItemStackLimit(final ItemStack stack)
    {
        return SlotFuel.isBucket(stack) ? 1 : super.getItemStackLimit(stack);
    }

    public static boolean isBucket(final ItemStack stack)
    {
        return stack.getItem() == Items.BUCKET;
    }
}
