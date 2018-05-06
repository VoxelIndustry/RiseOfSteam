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
import net.ros.common.tile.machine.TileSolidBoiler;
import net.ros.common.tile.module.SteamBoilerModule;

import java.util.Collections;

public class GuiSolidBoiler extends GuiMachineBase<TileSolidBoiler>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(ROSConstants.MODID, "textures/gui/boiler" +
            ".png");

    public GuiSolidBoiler(final EntityPlayer player, final TileSolidBoiler boiler)
    {
        super(player, boiler, BACKGROUND);

        this.addFluidTank((IFluidTank) boiler.getModule(FluidStorageModule.class).getFluidHandler("water"),
                128, 7, 18, 73);
        this.addSteamTank(boiler.getModule(SteamModule.class).getInternalSteamHandler(), 151, 7, 18, 73);

        this.addAnimatedSprite(this::getBurnLeftScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(81).y(38).width(14).height(13).u(176).v(12)
                        .s(190).t(25).build()).direction(GuiProgress.StartDirection.TOP).revert(false).build());

        this.addAnimatedSprite(boiler.getModule(SteamBoilerModule.class)::getHeatScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(10).y(79).width(12).height(71).u(176).v(84)
                        .s(176 + 12).t(85 + 71).build()).direction(GuiProgress.StartDirection.TOP).revert(false)
                        .build());

        this.addTooltip(new GuiSpace(10, 8, 12, 71), () ->
                Collections.singletonList(this.getHeatTooltip(boiler.getModule(SteamBoilerModule.class)::getCurrentHeat,
                        boiler.getModule(SteamBoilerModule.class)::getMaxHeat)));
    }

    private int getBurnLeftScaled(final int pixels)
    {
        final int i = this.getMachine().getMaxBurnTime();

        if (i == 0)
            return 0;

        return this.getMachine().getCurrentBurnTime() * pixels / i;
    }
}
