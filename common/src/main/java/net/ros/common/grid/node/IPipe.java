package net.ros.common.grid.node;

import net.ros.common.grid.impl.CableGrid;

public interface IPipe<T extends CableGrid> extends  ITileCable<T>
{
    PipeType getType();
}
