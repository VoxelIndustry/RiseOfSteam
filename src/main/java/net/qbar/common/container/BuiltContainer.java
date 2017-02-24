package net.qbar.common.container;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.network.ContainerTankUpdatePacket;

public class BuiltContainer extends Container
{

    private final String                                                                           name;

    private final EntityPlayer                                                                     player;

    private final Predicate<EntityPlayer>                                                          canInteract;
    private final List<Range<Integer>>                                                             playerSlotRanges;
    private final List<Range<Integer>>                                                             tileSlotRanges;

    private final ArrayList<MutableTriple<BooleanSupplier, Consumer<Boolean>, Boolean>>            boolValues;
    private final ArrayList<MutableTriple<IntSupplier, IntConsumer, Short>>                        shortValues;
    private final ArrayList<MutableTriple<IntSupplier, IntConsumer, Integer>>                      integerValues;
    private final ArrayList<MutableTriple<Supplier<FluidStack>, Consumer<FluidStack>, FluidStack>> fluidValues;
    private List<Consumer<InventoryCrafting>>                                                      craftEvents;
    private Integer[]                                                                              integerParts;

    private final List<IInventory>                                                                 inventories;

    public BuiltContainer(final String name, final EntityPlayer player, final List<IInventory> inventories,
            final Predicate<EntityPlayer> canInteract, final List<Range<Integer>> playerSlotRange,
            final List<Range<Integer>> tileSlotRange)
    {
        this.player = player;
        this.name = name;

        this.canInteract = canInteract;

        this.playerSlotRanges = playerSlotRange;
        this.tileSlotRanges = tileSlotRange;

        this.boolValues = new ArrayList<>();
        this.shortValues = new ArrayList<>();
        this.integerValues = new ArrayList<>();
        this.fluidValues = new ArrayList<>();

        this.inventories = inventories;

        this.inventories.forEach(inventory -> inventory.openInventory(player));
    }

    public void addBoolSync(final List<Pair<BooleanSupplier, Consumer<Boolean>>> syncables)
    {
        for (final Pair<BooleanSupplier, Consumer<Boolean>> syncable : syncables)
            this.boolValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), false));
        this.boolValues.trimToSize();
    }

    public void addShortSync(final List<Pair<IntSupplier, IntConsumer>> syncables)
    {
        for (final Pair<IntSupplier, IntConsumer> syncable : syncables)
            this.shortValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), (short) 0));
        this.shortValues.trimToSize();
    }

    public void addIntegerSync(final List<Pair<IntSupplier, IntConsumer>> syncables)
    {
        for (final Pair<IntSupplier, IntConsumer> syncable : syncables)
            this.integerValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), 0));
        this.integerValues.trimToSize();
        this.integerParts = new Integer[this.integerValues.size()];
    }

    public void addFluidStackSync(final List<Pair<Supplier<FluidStack>, Consumer<FluidStack>>> syncables)
    {
        for (final Pair<Supplier<FluidStack>, Consumer<FluidStack>> syncable : syncables)
            this.fluidValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), null));
    }

    public void addCraftEvents(final List<Consumer<InventoryCrafting>> craftEvents)
    {
        this.craftEvents = craftEvents;
    }

    public void addCraftEvent(final Consumer<InventoryCrafting> craftEvent)
    {
        if (this.craftEvents == null)
            this.craftEvents = new ArrayList<>();
        this.craftEvents.add(craftEvent);
    }

    public void removeCraftEvent(final Consumer<InventoryCrafting> craftEvent)
    {
        if (this.craftEvents == null)
            this.craftEvents = new ArrayList<>();
        this.craftEvents.remove(craftEvent);
    }

    public void addSlot(final Slot slot)
    {
        this.addSlotToContainer(slot);
    }

    @Override
    public boolean canInteractWith(final EntityPlayer playerIn)
    {
        return this.canInteract.test(playerIn);
    }

    @Override
    public final void onCraftMatrixChanged(final IInventory inv)
    {
        if (!this.craftEvents.isEmpty())
            this.craftEvents.forEach(consumer -> consumer.accept((InventoryCrafting) inv));
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (final IContainerListener listener : this.listeners)
        {
            int i = 0;
            if (!this.shortValues.isEmpty())
                for (final MutableTriple<IntSupplier, IntConsumer, Short> value : this.shortValues)
                {
                    final short supplied = (short) value.getLeft().getAsInt();
                    if (supplied != value.getRight())
                    {
                        listener.sendProgressBarUpdate(this, i, supplied);
                        value.setRight(supplied);
                    }
                    i++;
                }

            if (!this.integerValues.isEmpty())
                for (final MutableTriple<IntSupplier, IntConsumer, Integer> value : this.integerValues)
                {
                    final int supplied = value.getLeft().getAsInt();
                    if (supplied != value.getRight())
                    {
                        listener.sendProgressBarUpdate(this, i, supplied >> 16);
                        listener.sendProgressBarUpdate(this, i + 1, (short) (supplied & 0xFFFF));
                        value.setRight(supplied);
                    }
                    i += 2;
                }
            if (!this.boolValues.isEmpty())
                for (final MutableTriple<BooleanSupplier, Consumer<Boolean>, Boolean> value : this.boolValues)
                {
                    final boolean supplied = value.getLeft().getAsBoolean();
                    if (supplied != value.getRight())
                    {
                        listener.sendProgressBarUpdate(this, i, supplied ? 1 : 0);
                        value.setRight(supplied);
                    }
                }
        }
        for (final MutableTriple<Supplier<FluidStack>, Consumer<FluidStack>, FluidStack> value : this.fluidValues)
        {
            final FluidStack supplied = value.getLeft().get();
            boolean update = false;
            if (supplied == null && value.getRight() != null)
                update = true;
            else if (value.getRight() == null && supplied != null)
                update = true;
            else if (supplied != null && value.getRight() != null
                    && (!supplied.equals(value.getRight()) || supplied.amount != value.getRight().amount))
                update = true;
            if (update)
            {
                new ContainerTankUpdatePacket(this.windowId, this.fluidValues.indexOf(value), supplied)
                        .sendTo(this.player);
                value.setRight(supplied);
            }
        }
    }

    @Override
    public void addListener(final IContainerListener listener)
    {
        super.addListener(listener);

        int i = 0;
        if (!this.shortValues.isEmpty())
            for (final MutableTriple<IntSupplier, IntConsumer, Short> value : this.shortValues)
            {
                final short supplied = (short) value.getLeft().getAsInt();

                listener.sendProgressBarUpdate(this, i, supplied);
                value.setRight(supplied);
                i++;
            }

        if (!this.integerValues.isEmpty())
            for (final MutableTriple<IntSupplier, IntConsumer, Integer> value : this.integerValues)
            {
                final int supplied = value.getLeft().getAsInt();

                listener.sendProgressBarUpdate(this, i, supplied >> 16);
                listener.sendProgressBarUpdate(this, i + 1, (short) (supplied & 0xFFFF));
                value.setRight(supplied);
                i += 2;
            }

        if (!this.boolValues.isEmpty())
            for (final MutableTriple<BooleanSupplier, Consumer<Boolean>, Boolean> value : this.boolValues)
            {
                final boolean supplied = value.getLeft().getAsBoolean();

                listener.sendProgressBarUpdate(this, i, supplied ? 1 : 0);
                value.setRight(supplied);
                i++;
            }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(final int id, final int value)
    {
        if (id < this.shortValues.size())
        {
            this.shortValues.get(id).getMiddle().accept((short) value);
            this.shortValues.get(id).setRight((short) value);
        }
        else if (id - this.shortValues.size() < this.integerValues.size() * 2)
        {
            if ((id - this.shortValues.size()) % 2 == 0)
                this.integerParts[(id - this.shortValues.size()) / 2] = value;
            else
                this.integerValues.get((id - this.shortValues.size()) / 2).getMiddle().accept(
                        (this.integerParts[(id - this.shortValues.size()) / 2] & 0xFFFF) << 16 | value & 0xFFFF);
        }
        else if (id - this.shortValues.size() + this.integerValues.size() * 2 < this.boolValues.size())
        {
            this.boolValues.get(id - this.shortValues.size() + this.integerValues.size() * 2).getMiddle()
                    .accept(value == 1);
            this.boolValues.get(id - this.shortValues.size() + this.integerValues.size() * 2).setRight(value == 1);
        }
    }

    @SideOnly(Side.CLIENT)
    public void updateTank(final int property, final FluidStack fluidStack)
    {
        this.fluidValues.get(property).getMiddle().accept(fluidStack);
    }

    @Override
    public ItemStack transferStackInSlot(final EntityPlayer player, final int index)
    {

        ItemStack originalStack = ItemStack.EMPTY;

        final Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            final ItemStack stackInSlot = slot.getStack();
            originalStack = stackInSlot.copy();

            boolean shifted = false;

            for (final Range<Integer> range : this.playerSlotRanges)
                if (range.contains(index))
                {
                    if (this.shiftToTile(stackInSlot))
                        shifted = true;
                    break;
                }

            if (!shifted)
                for (final Range<Integer> range : this.tileSlotRanges)
                    if (range.contains(index))
                    {
                        if (this.shiftToPlayer(stackInSlot))
                            shifted = true;
                        break;
                    }

            slot.onSlotChange(stackInSlot, originalStack);
            if (stackInSlot.getCount() <= 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
            if (stackInSlot.getCount() == originalStack.getCount())
                return ItemStack.EMPTY;
            slot.onTake(player, stackInSlot);
        }
        return originalStack;
    }

    protected boolean shiftItemStack(final ItemStack stackToShift, final int start, final int end)
    {
        boolean changed = false;
        if (stackToShift.isStackable())
        {
            for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++)
            {
                final Slot slot = this.inventorySlots.get(slotIndex);
                final ItemStack stackInSlot = slot.getStack();
                if (stackInSlot != ItemStack.EMPTY && ItemStack.areItemStacksEqual(stackInSlot, stackToShift)
                        && slot.isItemValid(stackToShift))
                {
                    final int resultingStackSize = stackInSlot.getCount() + stackToShift.getCount();
                    final int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
                    if (resultingStackSize <= max)
                    {
                        stackToShift.setCount(0);
                        stackInSlot.setCount(resultingStackSize);
                        slot.onSlotChanged();
                        changed = true;
                    }
                    else if (stackInSlot.getCount() < max)
                    {
                        stackToShift.shrink(max - stackInSlot.getCount());
                        stackInSlot.setCount(max);
                        slot.onSlotChanged();
                        changed = true;
                    }
                }
            }
        }
        if (stackToShift.getCount() > 0)
        {
            for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++)
            {
                final Slot slot = this.inventorySlots.get(slotIndex);
                ItemStack stackInSlot = slot.getStack();
                if (stackInSlot == ItemStack.EMPTY && slot.isItemValid(stackToShift))
                {
                    final int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
                    stackInSlot = stackToShift.copy();
                    stackInSlot.setCount(Math.min(stackToShift.getCount(), max));
                    stackToShift.setCount(-stackInSlot.getCount());
                    slot.putStack(stackInSlot);
                    slot.onSlotChanged();
                    changed = true;
                }
            }
        }
        return changed;
    }

    private boolean shiftToTile(final ItemStack stackToShift)
    {
        for (final Range<Integer> range : this.tileSlotRanges)
            if (this.shiftItemStack(stackToShift, range.getMinimum(), range.getMaximum() + 1))
                return true;
        return false;
    }

    private boolean shiftToPlayer(final ItemStack stackToShift)
    {
        for (final Range<Integer> range : this.playerSlotRanges)
            if (this.shiftItemStack(stackToShift, range.getMinimum(), range.getMaximum() + 1))
                return true;
        return false;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public void onContainerClosed(final EntityPlayer player)
    {
        super.onContainerClosed(player);
        this.inventories.forEach(inventory -> inventory.closeInventory(player));
    }
}