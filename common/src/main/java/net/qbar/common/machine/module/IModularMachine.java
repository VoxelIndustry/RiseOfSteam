package net.qbar.common.machine.module;

import net.qbar.common.machine.MachineDescriptor;
import org.yggard.hermod.IEventEmitter;

import java.util.Collection;

public interface IModularMachine extends IEventEmitter
{
    Collection<MachineModule> getModules();

    <T extends MachineModule> T getModule(Class<T> moduleClass);

    <T extends MachineModule> boolean hasModule(Class<T> moduleClass);

    MachineDescriptor getDescriptor();
}
