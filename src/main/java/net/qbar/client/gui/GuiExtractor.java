package net.qbar.client.gui;

import org.yggard.brokkgui.data.EAlignment;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Color;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.skin.GuiButtonSkin;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.container.ItemStackView;
import org.yggard.brokkgui.wrapper.container.ItemStackViewSkin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.qbar.QBar;
import net.qbar.common.card.FilterCard;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.card.PunchedCardDataManager.ECardType;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.slot.ListenerSlot;
import net.qbar.common.network.FilteredMachinePacket;
import net.qbar.common.tile.TileExtractor;

public class GuiExtractor extends BrokkGuiContainer<BuiltContainer>
{
    private static final int      xSize      = 176, ySize = 166;

    private static final Texture  BACKGROUND = new Texture(QBar.MODID + ":textures/gui/extractor.png", 0, 0,
            GuiExtractor.xSize / 256.0f, GuiExtractor.ySize / 256.0f);
    private static final Texture  SLOT       = new Texture(QBar.MODID + ":textures/gui/slot.png", 0, 0, 1, 1);

    private final TileExtractor   extractor;

    private final GuiRelativePane filterPane;
    private final GuiButton       whitelist;

    private final Background      whitelistBackground, whitelistHoveredBackground, blacklistBackground,
            blacklistHoveredBackground;

    public GuiExtractor(final EntityPlayer player, final TileExtractor extractor)
    {
        super(extractor.createContainer(player));
        this.setWidth(GuiExtractor.xSize);
        this.setHeight(GuiExtractor.ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.extractor = extractor;

        final GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);
        this.getMainPanel().setBackground(new Background(GuiExtractor.BACKGROUND));

        final GuiLabel title = new GuiLabel(extractor.getDisplayName().getFormattedText());
        title.setTextAlignment(EAlignment.LEFT_CENTER);
        mainPanel.addChild(title, 0.03f, 0.05f);

        this.filterPane = new GuiRelativePane();
        this.filterPane.setWidthRatio(1);
        this.filterPane.setHeightRatio(0.5f);
        mainPanel.addChild(this.filterPane, 0.5f, 0.25f);
        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(this::refreshSlots);

        this.whitelistBackground = new Background(Color.fromHex("#FAFAFA"));
        this.whitelistHoveredBackground = new Background(Color.fromHex("#F5F5F5"));

        this.blacklistBackground = new Background(Color.fromHex("#424242"));
        this.blacklistHoveredBackground = new Background(Color.fromHex("#616161"));

        this.whitelist = new GuiButton("WHITELIST");
        this.extractor.getWhitelistProperty().addListener((obs, oldValue, newValue) ->
        {
            this.whitelist.setText(newValue ? "WHITELIST" : "BLACKLIST");

            if (newValue)
            {
                ((GuiButtonSkin) this.whitelist.getSkin()).setBackground(this.whitelistBackground);
                ((GuiButtonSkin) this.whitelist.getSkin()).setHoveredBackground(this.whitelistHoveredBackground);
                this.whitelist.setTextColor(Color.fromHex("#000000", 0.87f));
            }
            else
            {
                ((GuiButtonSkin) this.whitelist.getSkin()).setBackground(this.blacklistBackground);
                ((GuiButtonSkin) this.whitelist.getSkin()).setHoveredBackground(this.blacklistHoveredBackground);
                this.whitelist.setTextColor(Color.fromHex("#ffffff", 0.87f));
            }
        });
        this.whitelist.setOnActionEvent(
                e -> new FilteredMachinePacket(extractor, !this.extractor.getWhitelistProperty().getValue())
                        .sendToServer());

        ((GuiButtonSkin) this.whitelist.getSkin()).setBackground(this.whitelistBackground);
        ((GuiButtonSkin) this.whitelist.getSkin()).setHoveredBackground(this.whitelistHoveredBackground);
        this.whitelist.setWidth(60);
        this.whitelist.setHeight(15);
    }

    private void refreshSlots(final ItemStack stack)
    {
        this.filterPane.clearChilds();
        if (stack.hasTagCompound())
        {
            final IPunchedCard card = PunchedCardDataManager.getInstance().readFromNBT(stack.getTagCompound());
            if (card != null && card.getID() == ECardType.FILTER.getID())
            {
                for (int i = 0; i < 9; i++)
                {
                    final int index = i;
                    final ItemStackView view = new ItemStackView(((FilterCard) card).stacks[index]);
                    view.setWidth(18);
                    view.setHeight(18);
                    ((ItemStackViewSkin) view.getSkin()).setBackground(new Background(GuiExtractor.SLOT));
                    this.filterPane.addChild(view, 0.7f + 0.104f * (i / 3), 0.25f + 0.22f * (i % 3));
                }
                this.filterPane.addChild(this.whitelist, 0.5f, 0.9f);
            }
        }
    }
}
