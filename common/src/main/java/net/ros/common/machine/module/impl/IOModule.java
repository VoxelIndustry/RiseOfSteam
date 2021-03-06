package net.ros.common.machine.module.impl;

import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.ros.common.inventory.MappedInventoryHandler;
import net.ros.common.machine.module.IModularMachine;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.fluid.RestrictedTank;
import net.ros.common.machine.FluidIOPoint;
import net.ros.common.machine.InputPoint;
import net.ros.common.machine.OutputPoint;
import net.ros.common.machine.component.AutomationComponent;
import net.ros.common.machine.component.IOComponent;
import net.ros.common.machine.module.ICapabilityModule;
import net.ros.common.machine.module.MachineModule;
import net.ros.common.multiblock.MultiblockComponent;
import net.ros.common.multiblock.MultiblockSide;
import net.ros.common.steam.SteamCapabilities;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.HashMap;

public class IOModule extends MachineModule implements ICapabilityModule
{
    private HashMap<MultiblockSide, RestrictedTank>         tankWrappers;
    @Getter
    private HashMap<MultiblockSide, MappedInventoryHandler> invWrappers;

    public IOModule(IModularMachine machine)
    {
        super(machine, "IOModule");

        IOComponent io = machine.getDescriptor().get(IOComponent.class);

        if (machine.hasModule(FluidStorageModule.class) && io.getFluidIO().length != 0)
        {
            this.tankWrappers = new HashMap<>();

            FluidStorageModule fluid = machine.getModule(FluidStorageModule.class);

            for (FluidIOPoint point : io.getFluidIO())
                this.tankWrappers.put(point.getSide(), new RestrictedTank(
                        fluid.getFluidHandler(point.getTankName()), point.isInput(), point.isOutput()));
        }

        // TODO : Refactor this code when items I/O is fully migrated from AutomationComponent to IOComponent
        if (machine.getDescriptor().has(AutomationComponent.class))
        {
            this.invWrappers = new HashMap<>();

            AutomationComponent automation = machine.getDescriptor().get(AutomationComponent.class);
            InventoryModule inventory = machine.getModule(InventoryModule.class);

            for (InputPoint point : automation.getInputs())
            {
                String inventoryName = "undefined".equals(point.getInventory()) ?
                        (machine.hasModule(CraftingModule.class) ? "crafting" : "basic") : point.getInventory();

                this.invWrappers.put(point.getSide(), new MappedInventoryHandler(
                        inventory.getInventory(inventoryName), point.getSlots(), true, false));
            }
            for (OutputPoint point : automation.getOutputs())
            {
                String inventoryName = "undefined".equals(point.getInventory()) ?
                        (machine.hasModule(CraftingModule.class) ? "crafting" : "basic") : point.getInventory();

                this.invWrappers.put(point.getSide(), new MappedInventoryHandler(
                        inventory.getInventory(inventoryName), point.getSlots(),
                        invWrappers.containsKey(point.getSide()), true));
            }
        }
    }

    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        IOComponent io = getMachine().getDescriptor().get(IOComponent.class);
        MultiblockSide current = getMachine().getDescriptor().has(MultiblockComponent.class) ?
                getMachine().getDescriptor().get(MultiblockComponent.class)
                        .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getMachine().getFacing())
                : new MultiblockSide(from, facing);

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && io != null)
            return this.tankWrappers != null && this.tankWrappers.containsKey(current);
        else if ((capability == SteamCapabilities.STEAM_HANDLER || capability == SteamCapabilities.STEAM_MACHINE)
                && io != null)
            return ArrayUtils.contains(io.getSteamIO(), current);
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY &&
                getMachine().getDescriptor().has(AutomationComponent.class))
            return this.invWrappers != null && this.invWrappers.containsKey(current);
        return false;
    }

    @Nullable
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        IOComponent io = getMachine().getDescriptor().get(IOComponent.class);
        MultiblockSide current = getMachine().getDescriptor().has(MultiblockComponent.class) ?
                getMachine().getDescriptor().get(MultiblockComponent.class)
                        .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getMachine().getFacing())
                : new MultiblockSide(from, facing);

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && io != null)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.tankWrappers.get(current));
        else if (capability == SteamCapabilities.STEAM_HANDLER && io != null &&
                ArrayUtils.contains(io.getSteamIO(), current))
            return SteamCapabilities.STEAM_HANDLER.cast(
                    this.getMachine().getModule(SteamModule.class).getSteamHandler());
        else if (capability == SteamCapabilities.STEAM_MACHINE && io != null &&
                ArrayUtils.contains(io.getSteamIO(), current))
            return SteamCapabilities.STEAM_MACHINE.cast(this.getMachine().getModule(SteamModule.class));
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.invWrappers.get(current));
        return null;
    }
}
