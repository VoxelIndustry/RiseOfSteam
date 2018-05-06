package net.ros.common.inventory;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

@Getter
public class InventoryHandler extends ItemStackHandler
{
    @Setter
    private IntConsumer onSlotChange;
    @Setter
    private Runnable    onLoad;

    private TIntIntMap                              slotLimits;
    private TIntObjectHashMap<Predicate<ItemStack>> slotFilters;

    @Setter
    private Consumer<EntityPlayer> onOpen;
    @Setter
    private Consumer<EntityPlayer> onClose;

    public InventoryHandler()
    {
        this(0);
    }

    public InventoryHandler(int size)
    {
        this(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    public InventoryHandler(NonNullList<ItemStack> stacks)
    {
        super(stacks);

        this.slotLimits = new TIntIntHashMap();
        this.slotFilters = new TIntObjectHashMap<>();
    }

    @Override
    protected void onLoad()
    {
        super.onLoad();

        if (this.onLoad != null)
            this.onLoad.run();
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);

        if (this.onSlotChange != null)
            this.onSlotChange.accept(slot);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        if (!this.slotAcceptStack(slot, stack))
            return;
        super.setStackInSlot(slot, stack);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (!this.slotAcceptStack(slot, stack))
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        if (!this.slotLimits.isEmpty() && this.slotLimits.containsKey(slot))
            return this.slotLimits.get(slot);
        return super.getSlotLimit(slot);
    }

    public NonNullList<ItemStack> getStacks()
    {
        return this.stacks;
    }

    public void setSlotLimit(int slot, int limit)
    {
        this.slotLimits.put(slot, limit);
    }

    public boolean slotAcceptStack(int slot, ItemStack stack)
    {
        if (!this.slotFilters.isEmpty() && this.slotFilters.containsKey(slot))
            return this.slotFilters.get(slot).test(stack);
        return true;
    }

    public void addSlotFilter(int slot, Predicate<ItemStack> filter)
    {
        this.slotFilters.put(slot, filter);
    }

    public void addSlotFilters(Predicate<ItemStack> filter, int... slots)
    {
        for (int slot : slots)
            this.addSlotFilter(slot, filter);
    }

    public void openInventory(EntityPlayer player)
    {
        if (this.onOpen != null)
            this.onOpen.accept(player);
    }

    public void closeInventory(EntityPlayer player)
    {
        if (this.onClose != null)
            this.onClose.accept(player);
    }

    public boolean isEmpty()
    {
        for (ItemStack stack : this.stacks)
            if (!stack.isEmpty())
                return false;
        return true;
    }
}
