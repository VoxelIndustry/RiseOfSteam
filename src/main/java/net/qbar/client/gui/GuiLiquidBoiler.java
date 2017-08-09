package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiSpace;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.tile.machine.TileLiquidBoiler;

import java.util.Collections;

public class GuiLiquidBoiler extends GuiMachineBase<TileLiquidBoiler>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID,
            "textures/gui/liquidboiler.png");

    public GuiLiquidBoiler(final EntityPlayer player, final TileLiquidBoiler boiler)
    {
        super(player, boiler, BACKGROUND);

        this.addFluidTank(boiler.getWaterTank(), 128, 7, 18, 73);
        this.addFluidTank(boiler.getFuelTank(), 79, 7, 18, 73);
        this.addSteamTank(boiler.getSteamTank(), 151, 7, 18, 73);

        this.addAnimatedSprite(this::getHeatScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(10).y(79).width(12).height(78).u(176).v(84)
                        .s(176 + 12).t(85 + 79).build()).direction(GuiProgress.StartDirection.TOP).revert(false)
                        .build());

        this.addTooltip(new GuiSpace(10, 8, 12, 71), () ->
                Collections.singletonList(this.getHeatTooltip(this.getMachine()::getHeat, this.getMachine()::getMaxHeat)));
    }

    private int getHeatScaled(final int pixels)
    {
        final int i = (int) this.getMachine().getMaxHeat();

        if (i == 0)
            return 0;

        return (int) Math.min(this.getMachine().getHeat() * pixels / i, pixels);
    }
}
