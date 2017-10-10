package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiSpace;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.QBarConstants;
import net.qbar.common.tile.machine.TileTinyMiningDrill;

import java.text.NumberFormat;
import java.util.Collections;

public class GuiTinyMiningDrill extends GuiMachineBase<TileTinyMiningDrill>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID,
            "textures/gui/tinyminingdrill" + ".png");

    public GuiTinyMiningDrill(final EntityPlayer player, final TileTinyMiningDrill miningdrill)
    {
        super(player, miningdrill, BACKGROUND);

        this.addAnimatedSprite(this::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(61).y(39).width(55).height(7).u(176).v(0).s
                        (176 + 55).t(7).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true).build());

        this.addTooltip(new GuiSpace(61, 39, 55, 7), () ->
                Collections.singletonList(String.valueOf(NumberFormat.getPercentInstance().format(this.getMachine().getProgress()))));
    }

    private int getProgressScaled(final int pixels)
    {
        return (int) (pixels * this.getMachine().getProgress());
    }
}
