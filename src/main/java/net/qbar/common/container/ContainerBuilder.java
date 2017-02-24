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
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraftforge.fluids.FluidStack;
import net.qbar.common.container.slot.ListenerSlot;

public class ContainerBuilder
{

    private final String                                   name;

    private final EntityPlayer                             player;
    private Predicate<EntityPlayer>                        canInteract = player -> true;

    final List<ListenerSlot>                               slots;
    final List<Range<Integer>>                             playerInventoryRanges, tileInventoryRanges;

    List<Pair<BooleanSupplier, Consumer<Boolean>>>         boolValues;
    final List<Pair<IntSupplier, IntConsumer>>             shortValues;
    final List<Pair<IntSupplier, IntConsumer>>             integerValues;
    List<Pair<Supplier<FluidStack>, Consumer<FluidStack>>> fluidValues;

    final List<Consumer<InventoryCrafting>>                craftEvents;

    List<IInventory>                                       inventories;

    public ContainerBuilder(final String name, final EntityPlayer player)
    {

        this.name = name;

        this.player = player;

        this.slots = new ArrayList<>();
        this.playerInventoryRanges = new ArrayList<>();
        this.tileInventoryRanges = new ArrayList<>();

        this.boolValues = new ArrayList<>();
        this.shortValues = new ArrayList<>();
        this.integerValues = new ArrayList<>();
        this.fluidValues = new ArrayList<>();

        this.craftEvents = new ArrayList<>();

        this.inventories = new ArrayList<>();
    }

    public ContainerBuilder interact(final Predicate<EntityPlayer> canInteract)
    {
        this.canInteract = canInteract;
        return this;
    }

    public ContainerPlayerInventoryBuilder player(final InventoryPlayer player)
    {
        return new ContainerPlayerInventoryBuilder(this, player);
    }

    public ContainerTileInventoryBuilder tile(final IInventory tile)
    {
        return new ContainerTileInventoryBuilder(this, tile);
    }

    void addPlayerInventoryRange(final Range<Integer> range)
    {
        this.playerInventoryRanges.add(range);
    }

    void addTileInventoryRange(final Range<Integer> range)
    {
        this.tileInventoryRanges.add(range);
    }

    public BuiltContainer create()
    {
        final BuiltContainer built = new BuiltContainer(this.name, this.player, this.inventories, this.canInteract,
                this.playerInventoryRanges, this.tileInventoryRanges);
        if (!this.boolValues.isEmpty())
            built.addBoolSync(this.boolValues);
        if (!this.shortValues.isEmpty())
            built.addShortSync(this.shortValues);
        if (!this.integerValues.isEmpty())
            built.addIntegerSync(this.integerValues);
        if (!this.fluidValues.isEmpty())
            built.addFluidStackSync(this.fluidValues);
        if (!this.craftEvents.isEmpty())
            built.addCraftEvents(this.craftEvents);

        this.slots.forEach(built::addSlot);

        this.slots.clear();
        return built;
    }
}
