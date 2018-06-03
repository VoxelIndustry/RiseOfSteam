package net.ros.common.container.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.ros.common.inventory.InventoryHandler;

import java.util.function.Consumer;

public class ListenerSlot extends SlotItemHandler
{
    private Consumer<ItemStack> onChange;

    public ListenerSlot(final IItemHandler inventory, final int index, final int x, final int y)
    {
        super(inventory, index, x, y);
    }

    public void setOnChange(final Consumer<ItemStack> onChange)
    {
        this.onChange = onChange;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();

        if (this.getItemHandler() instanceof InventoryHandler)
            ((InventoryHandler) this.getItemHandler()).notifySlotChange();

        if (this.onChange != null)
            this.onChange.accept(this.getStack());
    }
}
