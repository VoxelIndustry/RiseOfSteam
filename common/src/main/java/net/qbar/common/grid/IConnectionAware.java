package net.qbar.common.grid;

import net.minecraft.util.EnumFacing;
import net.qbar.common.grid.impl.CableGrid;

public interface IConnectionAware
{
    void connectTrigger(EnumFacing facing, CableGrid grid);

    void disconnectTrigger(EnumFacing facing, CableGrid grid);
}
