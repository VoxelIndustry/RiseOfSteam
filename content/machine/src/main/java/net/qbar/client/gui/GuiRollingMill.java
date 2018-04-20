package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.QBarConstants;
import net.qbar.common.machine.module.impl.CraftingModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.tile.machine.TileRollingMill;

public class GuiRollingMill extends GuiMachineBase<TileRollingMill>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID,
            "textures/gui/rollingmill.png");

    public GuiRollingMill(final EntityPlayer player, final TileRollingMill rollingmill)
    {
        super(player, rollingmill, BACKGROUND);

        this.addSteamTank(rollingmill.getModule(SteamModule.class)
                .getInternalSteamHandler(), 151, 7, 18, 73);
        this.addAnimatedSprite(rollingmill.getModule(CraftingModule.class)::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(79).y(34).width(25).height(16).u(176).v(14).s
                        (176 + 25).t(14 + 16).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());
    }
}
