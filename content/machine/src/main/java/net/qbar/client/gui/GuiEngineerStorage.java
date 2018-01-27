package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.tile.machine.TileEngineerStorage;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiEngineerStorage  extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/engineerstorage.png",
            0, 0, xSize / 256.0f, ySize / 256.0f);

    public GuiEngineerStorage(EntityPlayer player, TileEngineerStorage tile)
    {
        super(tile.createContainer(player));
        this.setWidth(xSize + 24);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.addStylesheet("/assets/qbar/css/engineer_workshop.css");

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);

        GuiAbsolutePane body = new GuiAbsolutePane();
        body.setWidth(xSize);
        body.setHeightRatio(1);
        body.setBackground(new Background(BACKGROUND));

        mainPanel.addChild(body, 23, 0);
        mainPanel.addChild(new EngineerTabPane(tile, tile.getType()), 0, 0);
    }
}
