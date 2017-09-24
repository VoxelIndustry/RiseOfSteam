package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.tile.machine.TileSteamFurnace;

public class GuiSteamFurnace extends GuiMachineBase<TileSteamFurnace>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/steamfurnace.png");

    public GuiSteamFurnace(final EntityPlayer player, final TileSteamFurnace steamfurnace)
    {
        super(player, steamfurnace, BACKGROUND);

        this.addSteamTank(steamfurnace.getSteamTank(), 151, 7, 18, 73);
        this.addAnimatedSprite(this.getMachine()::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(79).y(34).width(25).height(16).u(176).v(14).s
                        (176 + 25).t(14 + 16).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());
    }
}
