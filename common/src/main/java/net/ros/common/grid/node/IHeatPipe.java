package net.ros.common.grid.node;

import net.ros.common.grid.impl.HeatPipeGrid;

public interface IHeatPipe extends IPipe<HeatPipeGrid>
{
    float getLoss();

    float getHeatConductivity();
}
