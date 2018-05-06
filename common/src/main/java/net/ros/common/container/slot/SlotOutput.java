package net.ros.common.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SlotOutput extends ListenerSlot
{
    public SlotOutput(final IItemHandler inventoryIn, final int index, final int xPosition, final int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(final ItemStack stack)
    {
        return false;
    }
}
