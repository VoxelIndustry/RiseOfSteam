package net.qbar.common.grid;

import net.minecraft.util.EnumFacing;

public interface IConnectionAware
{
    void connectTrigger(EnumFacing facing);

    void disconnectTrigger(EnumFacing facing);
}
