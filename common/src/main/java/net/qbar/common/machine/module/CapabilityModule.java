package net.qbar.common.machine.module;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CapabilityModule extends MachineModule
{
    public CapabilityModule(IModularMachine machine)
    {
        super(machine, "CapabilityModule");
    }

    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        // TODO
        return false;
    }

    @Nullable
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        // TODO
        return null;
    }
}
