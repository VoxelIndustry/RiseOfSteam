package net.ros.common.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

@AllArgsConstructor
@Getter
public class MappedInventoryHandler implements IItemHandlerModifiable
{
    private final IItemHandlerModifiable compose;
    private final int[]                  slots;
    private final boolean                acceptInput;
    private final boolean                acceptOutput;

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        validateSlotIndex(slot);

        if (!acceptInput)
            return;
        this.compose.setStackInSlot(map(slot), stack);
    }

    @Override
    public int getSlots()
    {
        return slots.length;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        validateSlotIndex(slot);
        return this.compose.getStackInSlot(map(slot));
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        validateSlotIndex(slot);
        if (!acceptInput)
            return stack;

        return this.compose.insertItem(map(slot), stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        validateSlotIndex(slot);
        if (!acceptOutput)
            return ItemStack.EMPTY;

        return this.compose.extractItem(map(slot), amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        this.validateSlotIndex(slot);
        return this.compose.getSlotLimit(map(slot));
    }

    private int map(int slot)
    {
        return this.slots[slot];
    }

    private void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= slots.length)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slots.length + ")");
    }
}
