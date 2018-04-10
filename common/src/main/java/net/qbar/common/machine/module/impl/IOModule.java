package net.qbar.common.machine.module.impl;

import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.qbar.common.fluid.RestrictedTank;
import net.qbar.common.machine.FluidIOPoint;
import net.qbar.common.machine.InputPoint;
import net.qbar.common.machine.OutputPoint;
import net.qbar.common.machine.component.AutomationComponent;
import net.qbar.common.machine.component.IOComponent;
import net.qbar.common.machine.module.ICapabilityModule;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.MachineModule;
import net.qbar.common.multiblock.MultiblockComponent;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.steam.SteamCapabilities;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.HashMap;

public class IOModule extends MachineModule implements ICapabilityModule
{
    private HashMap<MultiblockSide, RestrictedTank>  tankWrappers;
    @Getter
    private HashMap<MultiblockSide, SidedInvWrapper> invWrappers;

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
            CraftingInventoryModule inventory = machine.getModule(CraftingInventoryModule.class);

            for (InputPoint point : automation.getInputs())
                this.invWrappers.put(point.getSide(), new SidedInvWrapper(inventory, EnumFacing.UP));
            for (OutputPoint point : automation.getOutputs())
            {
                // Check if side is already an input and make it a union of both
                if (this.invWrappers.containsKey(point.getSide()))
                    this.invWrappers.put(point.getSide(), new SidedInvWrapper(inventory, EnumFacing.NORTH));
                else
                    this.invWrappers.put(point.getSide(), new SidedInvWrapper(inventory, EnumFacing.DOWN));
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
