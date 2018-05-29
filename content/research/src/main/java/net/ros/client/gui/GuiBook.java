package net.ros.client.gui;

import net.ros.common.ROSConstants;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.gui.BrokkGuiScreen;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;

public class GuiBook extends BrokkGuiScreen
{
    public GuiBook()
    {
        super(0.5f, 0.5f, 350, 200);

        final GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);
        this.getMainPanel().setBackgroundTexture(new Texture(ROSConstants.MODID + ":textures/gui/extractor.png"));


        final GuiLabel title = new GuiLabel("Research Book");
        mainPanel.addChild(title, 5, 4);

        //this.addStylesheet("/assets/ros/css/filterview.css");

    }
}
