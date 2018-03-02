package net.qbar.common.machine.module;

import net.qbar.common.machine.MachineDescriptor;

import java.util.Collection;

public interface IModularMachine
{
    Collection<MachineModule> getModules();

    <T extends MachineModule> T getModule(Class<T> moduleClass);

    <T extends MachineModule> boolean hasModule(Class<T> moduleClass);

    MachineDescriptor getDescriptor();
}
