package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.ros.common.ROSConstants;
import net.ros.common.card.CardDataStorage;
import net.ros.common.card.CardDataStorage.ECardType;
import net.ros.common.card.FilterCard;
import net.ros.common.card.IPunchedCard;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.slot.ListenerSlot;
import net.ros.common.network.action.ServerActionBuilder;
import net.ros.common.tile.machine.TileSplitter;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.panel.GuiPane;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiSplitter extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 180, ySize = 198;

    private static final Texture BACKGROUND = new Texture(ROSConstants.MODID + ":textures/gui/splitter.png",
            0, 0, 1, 1);

    private final TileSplitter splitter;

    private final FilterViewPane viewLeft;
    private final FilterViewPane viewForward;
    private final FilterViewPane viewRight;

    public GuiSplitter(final EntityPlayer player, final TileSplitter splitter)
    {
        super(splitter.createContainer(player));
        this.setWidth(GuiSplitter.xSize);
        this.setHeight(GuiSplitter.ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.addStylesheet("/assets/ros/css/filterview.css");
        this.splitter = splitter;

        final GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);
        this.getMainPanel().setBackgroundTexture(GuiSplitter.BACKGROUND);

        final GuiLabel title = new GuiLabel(splitter.getDisplayName().getFormattedText());
        mainPanel.addChild(title, 5, 4);

        this.viewLeft = new FilterViewPane(e -> new ServerActionBuilder("WHITELIST").toTile(splitter)
                .withInt("facing", this.splitter.getFacing().rotateY().ordinal())
                .withBoolean("whitelist", !this.splitter.getWhitelistProperty().get(0)).send());
        this.viewLeft.setxTranslate(5);
        this.viewLeft.setyTranslate(21);
        this.viewLeft.setVisible(false);
        mainPanel.addChild(viewLeft, 0, 0);

        this.viewForward = new FilterViewPane(e -> new ServerActionBuilder("WHITELIST").toTile(splitter)
                .withInt("facing", this.splitter.getFacing().getOpposite().ordinal())
                .withBoolean("whitelist", !this.splitter.getWhitelistProperty().get(1)).send());
        this.viewForward.setxTranslate(62);
        this.viewForward.setyTranslate(21);
        this.viewForward.setVisible(false);
        mainPanel.addChild(viewForward, 0, 0);

        this.viewRight = new FilterViewPane(e -> new ServerActionBuilder("WHITELIST").toTile(splitter)
                .withInt("facing", this.splitter.getFacing().rotateYCCW().ordinal())
                .withBoolean("whitelist", !this.splitter.getWhitelistProperty().get(2)).send());
        this.viewRight.setxTranslate(119);
        this.viewRight.setyTranslate(21);
        this.viewRight.setVisible(false);
        mainPanel.addChild(viewRight, 0, 0);

        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(stack -> this.refreshSlots(stack, viewLeft));
        ((ListenerSlot) this.getContainer().getSlot(37)).setOnChange(stack -> this.refreshSlots(stack, viewForward));
        ((ListenerSlot) this.getContainer().getSlot(38)).setOnChange(stack -> this.refreshSlots(stack, viewRight));

        this.getListeners().attach(this.splitter.getWhitelistProperty(), obs -> this.refreshWhitelists());
        this.refreshWhitelists();

        GuiAbsolutePane arrowPane = new GuiAbsolutePane();
        arrowPane.setWidth(160);
        arrowPane.setHeight(46);
        arrowPane.setOpacity(0.7f);
        arrowPane.addStyleClass("arrows");
        mainPanel.addChild(arrowPane, 10, 25);

        for (int i = 0; i < 3; i++)
        {
            EnumFacing facing = i == 0 ? splitter.getFacing().rotateY() : i == 1 ? splitter.getFacing().getOpposite()
                    : splitter.getFacing().rotateYCCW();

            if (!splitter.hasBelt(facing))
            {
                GuiPane invalid = new GuiPane();
                invalid.setWidth(50);
                invalid.setHeight(50);
                invalid.addStyleClass("invalid");
                mainPanel.addChild(invalid, 8 + (i * 57), 24);
            }
        }
    }

    private void refreshSlots(ItemStack stack, FilterViewPane pane)
    {
        if (stack.hasTagCompound())
        {
            final IPunchedCard card = CardDataStorage.instance().read(stack.getTagCompound());
            if (card != null && card.getID() == ECardType.FILTER.getID())
            {
                if (!pane.isVisible())
                    pane.setVisible(true);
                pane.refreshSlots((FilterCard) card);
            }
            else if (pane.isVisible())
                pane.setVisible(false);
        }
        else if (pane.isVisible())
            pane.setVisible(false);
    }

    private void refreshWhitelists()
    {
        for (int i = 0; i < 3; i++)
        {
            if (i == 0)
                this.viewLeft.setWhitelist(this.splitter.getWhitelistProperty().get(i));
            else if (i == 1)
                this.viewForward.setWhitelist(this.splitter.getWhitelistProperty().get(i));
            else
                this.viewRight.setWhitelist(this.splitter.getWhitelistProperty().get(i));
        }
    }
}
