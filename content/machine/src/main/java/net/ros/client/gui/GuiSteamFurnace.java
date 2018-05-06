package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.ros.client.gui.util.GuiMachineBase;
import net.ros.common.ROSConstants;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.client.gui.util.GuiProgress;
import net.ros.client.gui.util.GuiTexturedSpace;
import net.ros.common.machine.module.impl.CraftingModule;
import net.ros.common.tile.machine.TileSteamFurnace;

public class GuiSteamFurnace extends GuiMachineBase<TileSteamFurnace>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(ROSConstants.MODID,
            "textures/gui/steamfurnace.png");

    public GuiSteamFurnace(final EntityPlayer player, final TileSteamFurnace steamfurnace)
    {
        super(player, steamfurnace, BACKGROUND);

        this.addSteamTank(steamfurnace.getModule(SteamModule.class)
                .getInternalSteamHandler(), 151, 7, 18, 73);
        this.addAnimatedSprite(steamfurnace.getModule(CraftingModule.class)::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(79).y(34).width(25).height(16).u(176).v(14).s
                        (176 + 25).t(14 + 16).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());
    }
}
