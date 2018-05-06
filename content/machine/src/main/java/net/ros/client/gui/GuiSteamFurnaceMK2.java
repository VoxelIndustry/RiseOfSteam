package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.ros.client.gui.util.GuiMachineBase;
import net.ros.client.gui.util.GuiProgress;
import net.ros.client.gui.util.GuiSpace;
import net.ros.client.gui.util.GuiTexturedSpace;
import net.ros.common.ROSConstants;
import net.ros.common.machine.module.impl.CraftingModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.tile.machine.TileSteamFurnaceMK2;
import net.ros.common.tile.module.SteamHeaterModule;

import java.util.Collections;

public class GuiSteamFurnaceMK2 extends GuiMachineBase<TileSteamFurnaceMK2>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(ROSConstants.MODID,
            "textures/gui/steamfurnace.png");

    public GuiSteamFurnaceMK2(final EntityPlayer player, final TileSteamFurnaceMK2 steamfurnace)
    {
        super(player, steamfurnace, BACKGROUND);

        this.addAnimatedSprite(steamfurnace.getModule(SteamHeaterModule.class)::getHeatScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(10).y(79).width(12).height(71).u(176).v(102)
                        .s(176 + 12).t(102 + 71).build()).direction(GuiProgress.StartDirection.TOP).revert(false)
                        .build());

        this.addTooltip(new GuiSpace(10, 8, 12, 71), () ->
                Collections.singletonList(this.getHeatTooltip(
                        steamfurnace.getModule(SteamHeaterModule.class)::getCurrentHeat,
                        steamfurnace.getModule(SteamHeaterModule.class)::getMaxHeat)));

        this.addSteamTank(steamfurnace.getModule(SteamModule.class)
                .getInternalSteamHandler(), 151, 7, 18, 73);
        this.addAnimatedSprite(steamfurnace.getModule(CraftingModule.class)::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(79).y(34).width(25).height(16).u(176).v(14).s
                        (176 + 25).t(14 + 16).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());
    }
}
