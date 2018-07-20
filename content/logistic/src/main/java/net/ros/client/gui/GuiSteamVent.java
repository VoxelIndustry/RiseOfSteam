package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.ros.common.ROSConstants;
import net.ros.common.container.BuiltContainer;
import net.ros.common.network.action.ServerActionBuilder;
import net.ros.common.tile.TileSteamVent;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiSteamVent extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture       BACKGROUND = new Texture(ROSConstants.MODID + ":textures/gui/steamvent.png");
    private final        TileSteamVent vent;

    public GuiSteamVent(EntityPlayer player, TileSteamVent vent)
    {
        super(vent.createContainer(player));

        this.vent = vent;

        this.setWidth(xSize);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);
        this.getMainPanel().setBackgroundTexture(BACKGROUND);

        GuiLabel title = new GuiLabel(vent.getDisplayName().getFormattedText());
        mainPanel.addChild(title, 5, 4);

        PressureControlPane pane = new PressureControlPane(vent.getMaxPressure(), 0.25f,
                vent::getVentPressure, this::syncVentPressure, vent.getBufferTank());

        mainPanel.addChild(pane, this.getWidth() / 2 - pane.getWidth() / 2, 81 - pane.getHeight());

        this.addStylesheet("/assets/ros/css/steampressure.css");
    }

    private void syncVentPressure(Float ventPressure)
    {
        new ServerActionBuilder("ventpressure").withFloat("ventpressure", ventPressure).toTile(vent).send();
    }
}
