package net.ros.common.grid;

import net.minecraft.util.EnumFacing;
import net.ros.common.grid.impl.CableGrid;

public interface IConnectionAware
{
    void connectTrigger(EnumFacing facing, CableGrid grid);

    void disconnectTrigger(EnumFacing facing, CableGrid grid);
}
