package net.qbar.common.machine;

public interface IMachineComponent
{
    void setDescriptor(MachineDescriptor descriptor);

    MachineDescriptor getDescriptor();
}
