package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiSpace;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.QBarConstants;
import net.qbar.common.machine.module.impl.FluidStorageModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.tile.machine.TileLiquidBoiler;
import net.qbar.common.tile.module.SteamBoilerModule;

import java.util.Collections;

public class GuiLiquidBoiler extends GuiMachineBase<TileLiquidBoiler>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID,
            "textures/gui/liquidboiler.png");

    public GuiLiquidBoiler(final EntityPlayer player, final TileLiquidBoiler boiler)
    {
        super(player, boiler, BACKGROUND);

        this.addFluidTank((IFluidTank) boiler.getModule(FluidStorageModule.class).getFluidHandler("water"),
                128, 7, 18, 73);
        this.addFluidTank((IFluidTank) boiler.getModule(FluidStorageModule.class).getFluidHandler("fuel"),
                79, 7, 18, 73);
        this.addSteamTank(boiler.getModule(SteamModule.class).getInternalSteamHandler(), 151, 7, 18, 73);

        this.addAnimatedSprite(boiler.getModule(SteamBoilerModule.class)::getHeatScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(10).y(79).width(12).height(71).u(176).v(84)
                        .s(176 + 12).t(85 + 71).build()).direction(GuiProgress.StartDirection.TOP).revert(false)
                        .build());

        this.addTooltip(new GuiSpace(10, 8, 12, 71), () ->
                Collections.singletonList(this.getHeatTooltip(boiler.getModule(SteamBoilerModule.class)::getCurrentHeat,
                        boiler.getModule(SteamBoilerModule.class)::getMaxHeat)));
    }
}
