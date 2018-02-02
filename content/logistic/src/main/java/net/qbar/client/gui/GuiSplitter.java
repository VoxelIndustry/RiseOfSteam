package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.CardDataStorage;
import net.qbar.common.card.CardDataStorage.ECardType;
import net.qbar.common.card.FilterCard;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.slot.ListenerSlot;
import net.qbar.common.network.action.ServerActionBuilder;
import net.qbar.common.tile.machine.TileSplitter;
import org.yggard.brokkgui.data.EAlignment;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiSplitter extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 180, ySize = 198;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/splitter.png",
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

        this.addStylesheet("/assets/qbar/css/filterview.css");
        this.splitter = splitter;

        final GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);
        this.getMainPanel().setBackground(new Background(GuiSplitter.BACKGROUND));

        final GuiLabel title = new GuiLabel(splitter.getDisplayName().getFormattedText());
        title.setExpandToText(true);
        title.setTextAlignment(EAlignment.LEFT_CENTER);
        mainPanel.addChild(title, 2, 2);

        this.viewLeft = new FilterViewPane(e -> new ServerActionBuilder("WHITELIST").toTile(splitter)
                .withInt("facing", this.splitter.getFacing().rotateY().ordinal())
                .withBoolean("whitelist", !this.splitter.getWhitelistProperty().get(0)).send());
        this.viewLeft.setxTranslate(5);
        this.viewLeft.setyTranslate(21);

        this.viewForward = new FilterViewPane(e -> new ServerActionBuilder("WHITELIST").toTile(splitter)
                .withInt("facing", this.splitter.getFacing().getOpposite().ordinal())
                .withBoolean("whitelist", !this.splitter.getWhitelistProperty().get(1)).send());
        this.viewForward.setxTranslate(62);
        this.viewForward.setyTranslate(21);

        this.viewRight = new FilterViewPane(e -> new ServerActionBuilder("WHITELIST").toTile(splitter)
                .withInt("facing", this.splitter.getFacing().rotateYCCW().ordinal())
                .withBoolean("whitelist", !this.splitter.getWhitelistProperty().get(2)).send());
        this.viewRight.setxTranslate(119);
        this.viewRight.setyTranslate(21);

        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(stack -> this.refreshSlots(stack, viewLeft));
        ((ListenerSlot) this.getContainer().getSlot(37)).setOnChange(stack -> this.refreshSlots(stack, viewForward));
        ((ListenerSlot) this.getContainer().getSlot(38)).setOnChange(stack -> this.refreshSlots(stack, viewRight));

        this.getListeners().attach(this.splitter.getWhitelistProperty(), obs -> this.refreshWhitelists());
        this.refreshWhitelists();

        GuiLabel labelLeft = new GuiLabel("Left");
        labelLeft.setExpandToText(true);
        labelLeft.setTextAlignment(EAlignment.MIDDLE_UP);
        mainPanel.addChild(labelLeft, 32, 21);

        GuiLabel labelForward = new GuiLabel("Forward");
        labelForward.setExpandToText(true);
        labelForward.setTextAlignment(EAlignment.MIDDLE_UP);
        mainPanel.addChild(labelForward, 89, 21);

        GuiLabel labelRight = new GuiLabel("Right");
        labelRight.setExpandToText(true);
        labelRight.setTextAlignment(EAlignment.MIDDLE_UP);
        mainPanel.addChild(labelRight, 116, 21);
    }

    private void refreshSlots(ItemStack stack, FilterViewPane pane)
    {
        if (stack.hasTagCompound())
        {
            final IPunchedCard card = CardDataStorage.instance().read(stack.getTagCompound());
            if (card != null && card.getID() == ECardType.FILTER.getID())
            {
                if (!this.getMainPanel().hasChild(pane))
                    ((GuiAbsolutePane) this.getMainPanel()).addChild(pane, 0, 0);
                pane.refreshSlots((FilterCard) card);
            }
            else if (this.getMainPanel().hasChild(pane))
                this.getMainPanel().removeChild(pane);
        }
        else if (this.getMainPanel().hasChild(pane))
            this.getMainPanel().removeChild(pane);
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
