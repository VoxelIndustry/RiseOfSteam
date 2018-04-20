package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.QBarConstants;
import net.qbar.common.machine.module.impl.CraftingModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.tile.machine.TileSawMill;

public class GuiSawMill extends GuiMachineBase<TileSawMill>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID, "textures/gui/sawmill.png");

    public GuiSawMill(final EntityPlayer player, final TileSawMill sawmill)
    {
        super(player, sawmill, BACKGROUND);

        this.addSteamTank(sawmill.getModule(SteamModule.class)
                .getInternalSteamHandler(), 151, 7, 18, 73);
        this.addAnimatedSprite(sawmill.getModule(CraftingModule.class)::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(79).y(34).width(25).height(16).u(176).v(14).s
                        (176 + 25).t(14 + 16).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());
    }
}
