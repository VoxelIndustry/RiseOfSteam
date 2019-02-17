package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.ros.common.ROSConstants;
import net.ros.common.tile.machine.TileEngineerStorage;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.steamlayer.container.BuiltContainer;

public class GuiEngineerStorage  extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(ROSConstants.MODID + ":textures/gui/engineerstorage.png",
            0, 0, xSize / 256.0f, ySize / 256.0f);

    public GuiEngineerStorage(EntityPlayer player, TileEngineerStorage tile)
    {
        super(tile.createContainer(player));
        this.setWidth(xSize + 24);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.addStylesheet("/assets/ros/css/engineer_workshop.css");

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);

        GuiAbsolutePane body = new GuiAbsolutePane();
        body.setWidth(xSize);
        body.setHeightRatio(1);
        body.setBackgroundTexture(BACKGROUND);

        mainPanel.addChild(body, 23, 0);
        mainPanel.addChild(new EngineerTabPane(tile, tile.getType()), 0, 0);
    }
}
