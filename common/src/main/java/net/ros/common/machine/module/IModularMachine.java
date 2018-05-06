package net.ros.common.machine.module;

import net.minecraft.util.EnumFacing;
import net.ros.common.machine.MachineDescriptor;
import org.yggard.hermod.IEventEmitter;

import java.util.Collection;

public interface IModularMachine extends IEventEmitter
{
    Collection<MachineModule> getModules();

    <T extends MachineModule> T getModule(Class<T> moduleClass);

    <T extends MachineModule> boolean hasModule(Class<T> moduleClass);

    MachineDescriptor getDescriptor();

    EnumFacing getFacing();
}
