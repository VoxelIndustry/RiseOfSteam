package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.FilterCard;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.card.PunchedCardDataManager.ECardType;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.slot.ListenerSlot;
import net.qbar.common.network.action.ServerActionBuilder;
import net.qbar.common.tile.machine.TileExtractor;
import org.yggard.brokkgui.data.EAlignment;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.container.ItemStackView;

public class GuiExtractor extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/extractor.png", 0, 0,
            GuiExtractor.xSize / 256.0f, GuiExtractor.ySize / 256.0f);
    private static final Texture SLOT       = new Texture(QBarConstants.MODID + ":textures/gui/slot.png", 0, 0, 1, 1);

    private final TileExtractor extractor;

    private final GuiAbsolutePane filterPane;
    private final GuiButton       whitelist;

    public GuiExtractor(final EntityPlayer player, final TileExtractor extractor)
    {
        super(extractor.createContainer(player));
        this.setWidth(GuiExtractor.xSize);
        this.setHeight(GuiExtractor.ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.addStylesheet("/assets/qbar/css/splitter.css");

        this.extractor = extractor;

        final GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);
        this.getMainPanel().setBackground(new Background(GuiExtractor.BACKGROUND));

        final GuiLabel title = new GuiLabel(extractor.getDisplayName().getFormattedText());
        title.setTextAlignment(EAlignment.LEFT_CENTER);
        mainPanel.addChild(title, 0.03f, 0.05f);

        this.filterPane = new GuiAbsolutePane();
        this.filterPane.setWidthRatio(1);
        this.filterPane.setHeightRatio(0.5f);
        mainPanel.addChild(this.filterPane, 0.5f, 0.25f);
        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(this::refreshSlots);

        this.whitelist = new GuiButton("WHITELIST");
        this.extractor.getWhitelistProperty().addListener((obs, oldValue, newValue) -> this.refreshWhitelist(newValue));
        this.refreshWhitelist(this.extractor.getWhitelistProperty().getValue());

        this.whitelist.setOnActionEvent(e -> new ServerActionBuilder("WHITELIST").toTile(extractor)
                .withInt("facing", EnumFacing.UP.ordinal())
                .withBoolean("whitelist", !this.extractor.getWhitelistProperty().getValue()).send());

        this.whitelist.setWidth(60);
        this.whitelist.setHeight(15);
    }

    private void refreshWhitelist(boolean whitelist)
    {
        this.whitelist.setText(whitelist ? "WHITELIST" : "BLACKLIST");

        if (whitelist)
            this.whitelist.getStyleClass().replace("blacklist", "whitelist");
        else
            this.whitelist.getStyleClass().replace("whitelist", "blacklist");
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
                    final ItemStackView view = new ItemStackView(((FilterCard) card).stacks[i]);
                    view.setWidth(18);
                    view.setHeight(18);
                    view.setBackground(new Background(GuiExtractor.SLOT));
                    view.setTooltip(true);
                    this.filterPane.addChild(view, 115 + 18 * (i / 3), 10+18 * (i % 3));
                }
                this.filterPane.addChild(this.whitelist, 58, 65);
            }
        }
    }
}
