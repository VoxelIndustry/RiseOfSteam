package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.ros.common.ROSConstants;
import net.ros.common.container.BuiltContainer;
import net.ros.common.network.action.ServerActionBuilder;
import net.ros.common.tile.TilePressureValve;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiPressureValve extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture           BACKGROUND = new Texture(ROSConstants.MODID + ":textures/gui/steamvent.png");
    private final        TilePressureValve valve;

    public GuiPressureValve(EntityPlayer player, TilePressureValve valve)
    {
        super(valve.createContainer(player));

        this.valve = valve;

        this.setWidth(xSize);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);
        this.getMainPanel().setBackgroundTexture(BACKGROUND);

        GuiLabel title = new GuiLabel(valve.getDisplayName().getFormattedText());
        mainPanel.addChild(title, 5, 4);

        PressureControlPane pane = new PressureControlPane(valve.getMaxPressure(), 0.25f,
                valve::getFillPressureLimit, this::syncMaxPressure, valve::getDrainPressureLimit,
                this::syncMinPressure, valve.getBufferTank());

        mainPanel.addChild(pane, this.getWidth() / 2 - pane.getWidth() / 2, 81 - pane.getHeight());

        this.addStylesheet("/assets/ros/css/steampressure.css");
    }

    private void syncMaxPressure(Float pressure)
    {
        new ServerActionBuilder("maxpressure").withFloat("pressure", pressure).toTile(valve).send();
    }

    private void syncMinPressure(Float pressure)
    {
        new ServerActionBuilder("minpressure").withFloat("pressure", pressure).toTile(valve).send();
    }
}