package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.ros.client.gui.util.GuiMachineBase;
import net.ros.client.gui.util.GuiProgress;
import net.ros.client.gui.util.GuiSpace;
import net.ros.client.gui.util.GuiTexturedSpace;
import net.ros.common.ROSConstants;
import net.ros.common.machine.module.impl.FluidStorageModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.tile.machine.TileSolarBoiler;
import net.ros.common.tile.module.SteamBoilerModule;

import java.util.Collections;

public class GuiSolarBoiler extends GuiMachineBase<TileSolarBoiler>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(ROSConstants.MODID,
            "textures/gui/solarboiler.png");

    public GuiSolarBoiler(final EntityPlayer player, final TileSolarBoiler boiler)
    {
        super(player, boiler, BACKGROUND);

        this.addFluidTank((IFluidTank) boiler.getModule(FluidStorageModule.class).getFluidHandler("water"),
                128, 7, 18, 73);
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
