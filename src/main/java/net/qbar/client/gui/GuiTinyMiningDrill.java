package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.tile.machine.TileTinyMiningDrill;

public class GuiTinyMiningDrill extends GuiMachineBase<TileTinyMiningDrill>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID,
            "textures/gui/tinyminingdrill" + ".png");

    public GuiTinyMiningDrill(final EntityPlayer player, final TileTinyMiningDrill miningdrill)
    {
        super(player, miningdrill, BACKGROUND);

        this.addAnimatedSprite(this::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(61).y(39).width(55).height(7).u(176).v(0).s
                        (176 + 55).t(7).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true).build());
    }

    private int getProgressScaled(final int pixels)
    {
        return (int) (pixels * this.getMachine().getProgress());
    }
}
