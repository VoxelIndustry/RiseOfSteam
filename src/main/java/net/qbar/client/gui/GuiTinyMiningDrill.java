package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.common.tile.machine.TileTank;

public class GuiTinyMiningDrill extends GuiMachineBase<TileTank>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/tinyminingdrill" +
            ".png");

    public GuiTinyMiningDrill(final EntityPlayer player, final TileTank fluidtank)
    {
        super(player, fluidtank, BACKGROUND);
    }
}
