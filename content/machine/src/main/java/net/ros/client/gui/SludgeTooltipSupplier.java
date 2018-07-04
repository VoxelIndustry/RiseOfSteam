package net.ros.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.IFluidTank;
import net.ros.common.ore.Ore;
import net.ros.common.ore.Ores;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SludgeTooltipSupplier
{
    private static NumberFormat percentFormatter = NumberFormat.getPercentInstance();

    public static List<String> get(IFluidTank tank)
    {
        if (tank.getFluidAmount() == 0 || !Ores.isSludge(tank.getFluid().getFluid()))
            return Collections.emptyList();
        Optional<Ore> ore = Ores.fromSludge(tank.getFluid().getFluid());

        if (ore.isPresent())
        {
            List<String> lines = new ArrayList<>();

            ore.get().getMinerals().forEach((mineral, value) ->
                    lines.add(TextFormatting.GRAY + I18n.format(mineral.getName()) + " : " + percentFormatter.format(value)));
            return lines;
        }
        return Collections.emptyList();
    }
}
