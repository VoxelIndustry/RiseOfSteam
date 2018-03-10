package net.qbar.common.machine.module.impl;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.machine.module.ICapabilityModule;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.MachineModule;

import javax.annotation.Nullable;

public class IOModule extends MachineModule implements ICapabilityModule
{
    public IOModule(IModularMachine machine)
    {
        super(machine, "IOModule");
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
