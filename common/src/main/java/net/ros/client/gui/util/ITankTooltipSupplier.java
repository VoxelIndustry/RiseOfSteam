package net.ros.client.gui.util;

import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

@FunctionalInterface
public interface ITankTooltipSupplier
{
    List<String> get(IFluidTank tank);
}
