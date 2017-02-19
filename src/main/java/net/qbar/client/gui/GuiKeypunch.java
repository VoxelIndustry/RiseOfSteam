package net.qbar.client.gui;

import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.tile.TileKeypunch;

public class GuiKeypunch extends BrokkGuiContainer<BuiltContainer>
{
    private static final int     xSize      = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(QBar.MODID + ":textures/gui/keypunch.png", 0, 0,
            GuiKeypunch.xSize / 256.0f, GuiKeypunch.ySize / 256.0f);

    private final TileKeypunch   keypunch;

    public GuiKeypunch(final EntityPlayer player, final TileKeypunch keypunch)
    {
        super(keypunch.createContainer(player));
        this.setWidth(GuiKeypunch.xSize);
        this.setHeight(GuiKeypunch.ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.keypunch = keypunch;

        final GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);

        mainPanel.setBackground(new Background(GuiKeypunch.BACKGROUND));

        this.getContainer().addCraftEvent(inventory ->
        {

        });
    }
}
