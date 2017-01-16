package net.qbar.common.container;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class ContainerTileInventoryBuilder
{

    private final IInventory       tile;
    private final ContainerBuilder parent;
    private final int              rangeStart;

    ContainerTileInventoryBuilder(final ContainerBuilder parent, final IInventory tile)
    {
        this.tile = tile;
        this.parent = parent;
        this.rangeStart = parent.slots.size();
    }

    public ContainerTileInventoryBuilder slot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new Slot(this.tile, index, x, y));
        return this;
    }

    public ContainerTileInventoryBuilder outputSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new SlotOutput(this.tile, index, x, y));
        return this;
    }

    public ContainerTileInventoryBuilder filterSlot(final int index, final int x, final int y,
            final Predicate<ItemStack> filter)
    {
        this.parent.slots.add(new FilteredSlot(this.tile, index, x, y).setFilter(filter));
        return this;
    }

    @SuppressWarnings("null")
    public ContainerTileInventoryBuilder fluidSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new FilteredSlot(this.tile, index, x, y).setFilter(
                stack -> stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP)));
        return this;
    }

    public ContainerTileInventoryBuilder fuelSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new SlotFurnaceFuel(this.tile, index, x, y));
        return this;
    }

    /**
     *
     * @param supplier
     *            The supplier must supply a variable holding inside a Short, it
     *            will be truncated by force.
     * @param setter
     *            The setter to call when the variable has been updated.
     */
    public ContainerTileInventoryBuilder syncShortValue(final IntSupplier supplier, final IntConsumer setter)
    {
        this.parent.shortValues.add(Pair.of(supplier, setter));
        return this;
    }

    /**
     *
     * @param supplier
     *            The supplier it can supply a variable holding in an Integer it
     *            will be split inside multiples shorts.
     * @param setter
     *            The setter to call when the variable has been updated.
     */
    public ContainerTileInventoryBuilder syncIntegerValue(final IntSupplier supplier, final IntConsumer setter)
    {
        this.parent.integerValues.add(Pair.of(supplier, setter));
        return this;
    }

    public ContainerTileInventoryBuilder onCraft(final Consumer<InventoryCrafting> onCraft)
    {
        this.parent.craftEvents.add(onCraft);
        return this;
    }

    public ContainerBuilder addInventory()
    {
        this.parent.tileInventoryRanges.add(Range.between(this.rangeStart, this.parent.slots.size() - 1));
        return this.parent;
    }
}
