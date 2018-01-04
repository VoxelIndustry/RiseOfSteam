package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiSpace;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.QBarConstants;
import net.qbar.common.tile.machine.TileSmallMiningDrill;

import java.util.Collections;

public class GuiSmallMiningDrill extends GuiMachineBase<TileSmallMiningDrill>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID,
            "textures/gui/smallminingdrill" + ".png");

    public GuiSmallMiningDrill(final EntityPlayer player, final TileSmallMiningDrill miningdrill)
    {
        super(player, miningdrill, BACKGROUND);

        this.addFluidTank(miningdrill.getFluidTank(), 128, 7, 18, 73);
        this.addSteamTank(miningdrill.getSteamTank(), 151, 7, 18, 73);

        this.addAnimatedSprite(this::getHeatScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(10).y(79).width(12).height(71).u(176).v(84)
                        .s(176 + 12).t(85 + 71).build()).direction(GuiProgress.StartDirection.TOP).revert(false)
                        .build());

        this.addTooltip(new GuiSpace(10, 8, 12, 71), () ->
                Collections.singletonList(this.getHeatTooltip(this.getMachine()::getHeat, this.getMachine()
                        ::getMaxHeat)));
    }

    private int getHeatScaled(final int pixels)
    {
        final int i = (int) this.getMachine().getMaxHeat();

        if (i == 0)
            return 0;

        return (int) Math.min(this.getMachine().getHeat() * pixels / i, pixels);
    }
}
