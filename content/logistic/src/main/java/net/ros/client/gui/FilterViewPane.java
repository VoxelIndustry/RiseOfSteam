package net.ros.client.gui;

import net.ros.common.card.FilterCard;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.event.ActionEvent;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.elements.ItemStackView;
import org.yggard.hermod.EventHandler;

public class FilterViewPane extends GuiAbsolutePane
{
    private GuiButton       whitelist;
    private ItemStackView[] views;

    public FilterViewPane(EventHandler<ActionEvent> buttonAction)
    {
        this.setWidth(56);
        this.setHeight(70);

        this.whitelist = new GuiButton("WHITELIST");
        whitelist.setOnActionEvent(buttonAction);
        whitelist.setWidth(56);
        whitelist.setHeight(15);
        this.addChild(whitelist, 0, 55);

        this.views = new ItemStackView[9];
    }

    public void setWhitelist(boolean whitelist)
    {
        if (whitelist)
        {
            this.whitelist.getLabel().setText("WHITELIST");
            this.getStyleClass().replace("blacklist", "whitelist");
        }
        else
        {
            this.whitelist.getLabel().setText("BLACKLIST");
            this.getStyleClass().replace("whitelist", "blacklist");
        }
    }

    public void refreshSlots(FilterCard card)
    {
        for (int i = 0; i < 9; i++)
        {
            if (this.views[i] == null)
            {
                ItemStackView view = new ItemStackView(card.stacks[i]);
                view.setWidth(18);
                view.setHeight(18);
                view.setItemTooltip(true);
                this.views[i] = view;
                this.addChild(view, 1 + 18 * (i % 3), 1 + 18 * (i / 3));
            }
            this.views[i].setItemStack(card.stacks[i]);
        }
    }
}
