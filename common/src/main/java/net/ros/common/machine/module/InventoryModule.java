package net.ros.common.machine.module;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.ros.common.inventory.InventoryHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

public class InventoryModule extends MachineModule implements ISerializableModule
{
    private final Map<String, InventoryHandler> inventories;
    private final Map<String, IntConsumer>      onSlotChangeEvents;

    @Getter
    private CombinedInvWrapper combinedInventory;

    public InventoryModule(IModularMachine machine, int slotCount)
    {
        super(machine, "InventoryModule");

        this.inventories = new HashMap<>();
        this.onSlotChangeEvents = new HashMap<>();

        if (slotCount != -1)
            this.addBasic(slotCount);
    }

    public InventoryModule(IModularMachine machine)
    {
        this(machine, -1);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.inventories.forEach((name, inv) -> inv.deserializeNBT(tag.getCompoundTag("Inv" + name)));
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        this.inventories.forEach((name, inv) -> tag.setTag("Inv" + name, inv.serializeNBT()));
        return tag;
    }

    public void addBasic(int slotCount)
    {
        this.addInventory("basic", new InventoryHandler(slotCount));
    }

    public InventoryModule addInventory(String name, InventoryHandler inventory)
    {
        this.inventories.put(name, inventory);

        inventory.setOnSlotChange(slot ->
        {
            this.getMachineTile().markDirty();

            if (this.onSlotChangeEvents.containsKey(name) && slot != -1)
                this.onSlotChangeEvents.get(name).accept(slot);
        });

        this.combinedInventory = new CombinedInvWrapper(this.inventories.values().toArray(new InventoryHandler[0]));
        return this;
    }

    public void setOnSlotChange(String name, IntConsumer onSlotChange)
    {
        this.onSlotChangeEvents.put(name, onSlotChange);
    }

    public InventoryHandler getInventory(String name)
    {
        return this.inventories.get(name);
    }

    public boolean hasInventory(String name)
    {
        return this.inventories.containsKey(name);
    }
}
