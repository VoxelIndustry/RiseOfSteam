package net.ros.common.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SlotDisplay extends ListenerSlot
{
    public SlotDisplay(final IItemHandler inventory, final int index, final int x, final int y)
    {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(final ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean getHasStack()
    {
        return false;
    }

    @Override
    public ItemStack decrStackSize(final int par1)
    {
        return ItemStack.EMPTY;
    }
}
