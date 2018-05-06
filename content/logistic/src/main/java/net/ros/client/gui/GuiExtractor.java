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
import net.ros.common.tile.machine.TileExtractor;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiExtractor extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 190;

    private static final Texture BACKGROUND = new Texture(ROSConstants.MODID + ":textures/gui/extractor.png", 0, 0,
            1, 1);

    private final TileExtractor extractor;

    private final FilterViewPane viewPane;

    public GuiExtractor(final EntityPlayer player, final TileExtractor extractor)
    {
        super(extractor.createContainer(player));
        this.setWidth(GuiExtractor.xSize);
        this.setHeight(GuiExtractor.ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.addStylesheet("/assets/ros/css/filterview.css");

        this.extractor = extractor;

        final GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);
        this.getMainPanel().setBackgroundTexture(GuiExtractor.BACKGROUND);

        final GuiLabel title = new GuiLabel(extractor.getDisplayName().getFormattedText());
        mainPanel.addChild(title, 5, 4);

        this.viewPane = new FilterViewPane(e -> new ServerActionBuilder("WHITELIST").toTile(extractor)
                .withInt("facing", EnumFacing.UP.ordinal())
                .withBoolean("whitelist", !this.extractor.getWhitelistProperty().getValue()).send());
        this.viewPane.setVisible(false);
        mainPanel.addChild(viewPane, 60, 14);

        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(this::refreshSlots);

        this.getListeners().attach(this.extractor.getWhitelistProperty(),
                (obs, oldValue, newValue) -> viewPane.setWhitelist(newValue));
        viewPane.setWhitelist(this.extractor.getWhitelistProperty().getValue());
    }

    private void refreshSlots(final ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            final IPunchedCard card = CardDataStorage.instance().read(stack.getTagCompound());
            if (card != null && card.getID() == ECardType.FILTER.getID())
            {
                if (!this.viewPane.isVisible())
                    this.viewPane.setVisible(true);
                this.viewPane.refreshSlots((FilterCard) card);
            }
            else if (this.viewPane.isVisible())
                this.viewPane.setVisible(false);
        }
        else if (this.viewPane.isVisible())
            this.viewPane.setVisible(false);
    }
}
