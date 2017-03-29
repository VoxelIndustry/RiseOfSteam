package net.qbar.client.gui;

import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.tile.machine.TileAssembler;

public class GuiAssembler extends BrokkGuiContainer<BuiltContainer>
{
    private static final int     xSize      = 176, ySize = 206;

    private static final Texture BACKGROUND = new Texture(QBar.MODID + ":textures/gui/assembler.png", 0, 0,
            xSize / 256.0f, ySize / 256.0f);

    private final TileAssembler  assembler;

    public GuiAssembler(final EntityPlayer player, final TileAssembler assembler)
    {
        super(assembler.createContainer(player));
        this.setWidth(xSize);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.assembler = assembler;

        final GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);

        mainPanel.setBackground(new Background(BACKGROUND));
    }
}
