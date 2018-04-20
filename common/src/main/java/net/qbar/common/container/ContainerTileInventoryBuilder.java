package net.qbar.common.container;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.qbar.common.container.slot.*;
import net.qbar.common.container.sync.DefaultSyncables;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.SteamCapabilities;
import org.apache.commons.lang3.Range;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ContainerTileInventoryBuilder
{
    private final ItemStackHandler inventory;
    private final ContainerBuilder parent;
    private final int              rangeStart;

    ContainerTileInventoryBuilder(final ContainerBuilder parent, final ItemStackHandler inventory)
    {
        this.inventory = inventory;
        this.parent = parent;
        this.rangeStart = parent.slots.size();
    }

    /**
     * Utility method to add a line of filtered slots.
     *
     * @param indexStart the slot index at which the line begins
     * @param x          the horizontal position at which the line begins
     * @param y          the vertical position at which the line begins
     * @param quantity   the number of slot to be added
     * @param axis       the {@link EnumFacing.Axis} to follow
     * @param filter     the {@link ItemStack} predicate to use for filtering logic
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder filterSlotLine(int indexStart, int x, int y, int quantity,
                                                        EnumFacing.Axis axis, Predicate<ItemStack> filter)
    {
        for (int i = 0; i < quantity; i++)
        {
            if (axis.isHorizontal())
                this.filterSlot(indexStart + i, x + (i * 18), y, filter);
            else
                this.filterSlot(indexStart + i, x, y + (i * 18), filter);
        }
        return this;
    }

    /**
     * Utility method to add a line of slots.
     *
     * @param indexStart the slot index at which the line begins
     * @param x          the horizontal position at which the line begins
     * @param y          the vertical position at which the line begins
     * @param quantity   the number of slot to be added
     * @param axis       the {@link EnumFacing.Axis} to follow
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder slotLine(int indexStart, int x, int y, int quantity, EnumFacing.Axis axis)
    {
        for (int i = 0; i < quantity; i++)
        {
            if (axis.isHorizontal())
                this.slot(indexStart + i, x + (i * 18), y);
            else
                this.slot(indexStart + i, x, y + (i * 18));
        }
        return this;
    }

    /**
     * Add a default slot to the slot list of the current {@code IInventory}.
     *
     * @param index the index this slot will use to communicate with the inventory.
     * @param x     the horizontal position at which the slot is placed
     * @param y     the vertical position at which the slot is placed
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder slot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new ListenerSlot(this.inventory, index, x, y));
        return this;
    }

    /**
     * Add an output slot to the slot list of the current {@code IInventory}.
     * This slot will be give-only, stacks cannot be manually placed inside.
     *
     * @param index the index this slot will use to communicate with the inventory.
     * @param x     the horizontal position at which the slot is placed
     * @param y     the vertical position at which the slot is placed
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder outputSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new SlotOutput(this.inventory, index, x, y));
        return this;
    }

    /**
     * Add a recipe slot to the slot list of the current {@code IInventory}.
     * This slot will be a special filtered slot that only accept stacks matching a specific
     * {@link net.qbar.common.recipe.category.QBarRecipeCategory}
     * <p>
     * Note that the filter will not take the amount of items into account, only the type is enforced.
     *
     * @param index      the index this slot will use to communicate with the inventory.
     * @param recipeID   the id of the {@link net.qbar.common.recipe.category.QBarRecipeCategory} to use
     * @param recipeSlot the index of the recipe slot to match against
     * @param x          the horizontal position at which the slot is placed
     * @param y          the vertical position at which the slot is placed
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder recipeSlot(final int index, final String recipeID, final int recipeSlot,
                                                    final int x, final int y)
    {
        this.parent.slots.add(new FilteredSlot(this.inventory, index, x, y)
                .setFilter(stack -> QBarRecipeHandler.inputMatchWithoutCount(recipeID, recipeSlot, stack)));
        return this;
    }

    /**
     * Add a recipe slot to the slot list of the current {@code IInventory}.
     * This slot will be a special filtered slot that only accept stacks matching a specific
     * {@link net.qbar.common.recipe.category.QBarRecipeCategory} and another filter.
     * <p>
     * Note that the filter will not take the amount of items into account, only the type is enforced.
     * The second filter differ from the usual filterSlot predicate by using the slot index instead of the contained
     * stack. It allow to make slot-dependent logic.
     *
     * @param index      the index this slot will use to communicate with the inventory.
     * @param recipeID   the id of the {@link net.qbar.common.recipe.category.QBarRecipeCategory} to use
     * @param recipeSlot the index of the recipe slot to match against
     * @param x          the horizontal position at which the slot is placed
     * @param y          the vertical position at which the slot is placed
     * @param predicate  the second filter to apply to this slot
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder recipeSlot(final int index, final String recipeID, final int recipeSlot,
                                                    final int x, final int y, final Predicate<Integer> predicate)
    {
        this.parent.slots.add(new FilteredSlot(this.inventory, index, x, y).setFilter(stack -> predicate.test(index)
                && QBarRecipeHandler.inputMatchWithoutCount(recipeID, recipeSlot, stack)));
        return this;
    }

    /**
     * Add a filter slot to the slot list of the current {@code IInventory}.
     * This slot will be a special filtered slot that only accept stacks matching a specified {@link ItemStack}
     * predicate.
     *
     * @param index  the index this slot will use to communicate with the inventory.
     * @param x      the horizontal position at which the slot is placed
     * @param y      the vertical position at which the slot is placed
     * @param filter a predicate using the current {@link ItemStack} queried for the filter logic
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder filterSlot(final int index, final int x, final int y,
                                                    final Predicate<ItemStack> filter)
    {
        this.parent.slots.add(new FilteredSlot(this.inventory, index, x, y).setFilter(filter));
        return this;
    }

    /**
     * Add a fluid-containers-only slot to the slot list of the current {@code IInventory}
     * <p>
     * The builtin filter will only allow {@link ItemStack} having the {@code CapabilityFluidHandler
     * .FLUID_HANDLER_ITEM_CAPABILITY} with {@code EnumFacing.UP}
     *
     * @param index the index this slot will use to communicate with the inventory.
     * @param x     the horizontal position at which the slot is placed
     * @param y     the vertical position at which the slot is placed
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    @SuppressWarnings("null")
    public ContainerTileInventoryBuilder fluidSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new FilteredSlot(this.inventory, index, x, y).setFilter(
                stack -> stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP)));
        return this;
    }

    /**
     * Add a steam-containers-only slot to the slot list of the current {@code IInventory}
     * <p>
     * The builtin filter will only allow {@link ItemStack} having the {@code SteamCapabilities
     * .ITEM_STEAM_HANDLER} with {@code EnumFacing.UP}
     *
     * @param index the index this slot will use to communicate with the inventory.
     * @param x     the horizontal position at which the slot is placed
     * @param y     the vertical position at which the slot is placed
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    @SuppressWarnings("null")
    public ContainerTileInventoryBuilder steamSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new FilteredSlot(this.inventory, index, x, y).setFilter(
                stack -> stack.hasCapability(SteamCapabilities.ITEM_STEAM_HANDLER, EnumFacing.UP)));
        return this;
    }

    /**
     * Add a fuel-only slot to the slot list of the current {@code IInventory}
     * <p>
     * The builtin filter will query {@link net.minecraft.tileentity.TileEntityFurnace#isItemFuel(ItemStack)} with
     * the exception of allowing buckets for vanilla behavior compatibility.
     *
     * @param index the index this slot will use to communicate with the inventory.
     * @param x     the horizontal position at which the slot is placed
     * @param y     the vertical position at which the slot is placed
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder fuelSlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new SlotFuel(this.inventory, index, x, y));
        return this;
    }

    /**
     * Add a display only slot to the slot list of the current {@code IInventory}
     * <p>
     * ItemStacks are not allowed to be taken or deposited in this slot.
     * The contained ItemStack is guaranteed to be immutable to player interaction.
     *
     * @param index the index this slot will use to communicate with the inventory.
     * @param x     the horizontal position at which the slot is placed
     * @param y     the vertical position at which the slot is placed
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder displaySlot(final int index, final int x, final int y)
    {
        this.parent.slots.add(new SlotDisplay(this.inventory, index, x, y));
        return this;
    }

    /**
     * Sync a Boolean value between the server and the client
     *
     * @param supplier a supplier giving the value from the server
     * @param setter   a consumer used to set the value of the client
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder syncBooleanValue(final Supplier<Boolean> supplier,
                                                          final Consumer<Boolean> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableBoolean(supplier, setter));
        return this;
    }

    /**
     * Sync an Integer value between the server and the client
     *
     * @param supplier a supplier giving the value from the server
     * @param setter   a consumer used to set the value of the client
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder syncIntegerValue(final Supplier<Integer> supplier,
                                                          final Consumer<Integer> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableInteger(supplier, setter));
        return this;
    }

    /**
     * Sync a Float value between the server and the client
     *
     * @param supplier a supplier giving the value from the server
     * @param setter   a consumer used to set the value of the client
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder syncFloatValue(final Supplier<Float> supplier, final Consumer<Float> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableFloat(supplier, setter));
        return this;
    }

    /**
     * Sync a String value between the server and the client
     *
     * @param supplier a supplier giving the value from the server
     * @param setter   a consumer used to set the value of the client
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder syncStringValue(final Supplier<String> supplier, final Consumer<String> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableString(supplier, setter));
        return this;
    }

    /**
     * Sync a {@link FluidStack} value between the server and the client
     *
     * @param supplier a supplier giving the value from the server
     * @param setter   a consumer used to set the value of the client
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder syncFluidValue(final Supplier<FluidStack> supplier,
                                                        final Consumer<FluidStack> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableFluid(supplier, setter));
        return this;
    }

    /**
     * Sync an {@link ItemStack} value between the server and the client
     *
     * @param supplier a supplier giving the value from the server
     * @param setter   a consumer used to set the value of the client
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerTileInventoryBuilder syncItemValue(final Supplier<ItemStack> supplier,
                                                       final Consumer<ItemStack> setter)
    {
        this.parent.syncables.add(new DefaultSyncables.SyncableItem(supplier, setter));
        return this;
    }

    /**
     * Should not be used, this method is linked to a dysfunctional system.
     * A rework to implement this feature is scheduled.
     *
     * @param onCraft a consumer using an {@link InventoryCrafting} to show the result of the craft matrix evaluation
     * @return a reference to this {@code ContainerTileInventoryBuilder} to resume the "Builder" pattern
     */
    @Deprecated
    public ContainerTileInventoryBuilder onCraft(final Consumer<InventoryCrafting> onCraft)
    {
        this.parent.craftEvents.add(onCraft);
        return this;
    }

    /**
     * Close this builder and add the slot list to the current {@link BuiltContainer} construction.
     *
     * @return the parent {@link ContainerBuilder} to resume the "Builder" pattern
     */
    public ContainerBuilder addInventory()
    {
        if (this.inventory.getSlots() != 0)
            this.parent.tileInventoryRanges.add(Range.between(this.rangeStart, this.parent.slots.size() - 1));
        this.parent.inventories.add(this.inventory);
        return this.parent;
    }
}
