package net.qbar.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.yggard.brokkgui.data.EAlignment;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Color;
import org.yggard.brokkgui.paint.GuiPaint;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
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
import net.qbar.common.tile.machine.TileSplitter;

public class GuiSplitter extends BrokkGuiContainer<BuiltContainer>
{
    private static final int            xSize      = 176, ySize = 206;

    private static final Texture        BACKGROUND = new Texture(QBar.MODID + ":textures/gui/splitter.png", 0, 0,
            GuiSplitter.xSize / 256.0f, GuiSplitter.ySize / 256.0f);
    private static final Texture        SLOT       = new Texture(QBar.MODID + ":textures/gui/slot.png", 0, 0, 1, 1);

    private final TileSplitter          splitter;

    private final List<GuiAbsolutePane> filterPane;
    private final List<GuiButton>       whitelist;

    private final GuiPaint              whitelistBackground, whitelistHoveredBackground, blacklistBackground,
            blacklistHoveredBackground;

    public GuiSplitter(final EntityPlayer player, final TileSplitter splitter)
    {
        super(splitter.createContainer(player));
        this.setWidth(GuiSplitter.xSize);
        this.setHeight(GuiSplitter.ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.splitter = splitter;

        final GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);
        this.getMainPanel().setBackground(new Background(GuiSplitter.BACKGROUND));

        final GuiLabel title = new GuiLabel(splitter.getDisplayName().getFormattedText());
        title.setTextAlignment(EAlignment.LEFT_CENTER);
        mainPanel.addChild(title, 0.03f, 0.05f);

        this.filterPane = new ArrayList<>(3);

        final GuiAbsolutePane filter1 = new GuiAbsolutePane();
        filter1.setWidthRatio(1);
        filter1.setHeightRatio(0.18f);
        mainPanel.addChild(filter1, 0.5f, 0.16f);
        this.filterPane.add(filter1);

        final GuiAbsolutePane filter2 = new GuiAbsolutePane();
        filter2.setWidthRatio(1);
        filter2.setHeightRatio(0.18f);
        mainPanel.addChild(filter2, 0.5f, 0.335f);
        this.filterPane.add(filter2);

        final GuiAbsolutePane filter3 = new GuiAbsolutePane();
        filter3.setWidthRatio(1);
        filter3.setHeightRatio(0.18f);
        mainPanel.addChild(filter3, 0.5f, 0.508f);
        this.filterPane.add(filter3);

        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(stack -> this.refreshSlots(stack, 0));
        ((ListenerSlot) this.getContainer().getSlot(37)).setOnChange(stack -> this.refreshSlots(stack, 1));
        ((ListenerSlot) this.getContainer().getSlot(38)).setOnChange(stack -> this.refreshSlots(stack, 2));

        this.whitelistBackground = Color.fromHex("#FAFAFA");
        this.whitelistHoveredBackground = Color.fromHex("#F5F5F5");

        this.blacklistBackground = Color.fromHex("#424242");
        this.blacklistHoveredBackground = Color.fromHex("#616161");

        this.whitelist = new ArrayList<>();

        final GuiButton whitelist1 = new GuiButton("WHITELIST");
        this.splitter.getWhitelistProperty().addListener((obs) -> this.refreshWhitelists());
        whitelist1.setOnActionEvent(e -> new FilteredMachinePacket(splitter, this.splitter.getFacing().rotateY(),
                !this.splitter.getWhitelistProperty().get(0)).sendToServer());
        ((GuiButtonSkin) whitelist1.getSkin()).setBackground(new Background((Color) this.whitelistBackground));
        ((GuiButtonSkin) whitelist1.getSkin())
                .setHoveredBackground(new Background((Color) this.whitelistHoveredBackground));
        whitelist1.setWidth(55);
        whitelist1.setHeight(15);
        this.whitelist.add(whitelist1);

        final GuiButton whitelist2 = new GuiButton("WHITELIST");
        this.splitter.getWhitelistProperty().addListener((obs) -> this.refreshWhitelists());
        whitelist2.setOnActionEvent(e -> new FilteredMachinePacket(splitter, this.splitter.getFacing().getOpposite(),
                !this.splitter.getWhitelistProperty().get(1)).sendToServer());
        ((GuiButtonSkin) whitelist2.getSkin()).setBackground(new Background((Color) this.whitelistBackground));
        ((GuiButtonSkin) whitelist2.getSkin())
                .setHoveredBackground(new Background((Color) this.whitelistHoveredBackground));
        whitelist2.setWidth(55);
        whitelist2.setHeight(15);
        this.whitelist.add(whitelist2);

        final GuiButton whitelist3 = new GuiButton("WHITELIST");
        this.splitter.getWhitelistProperty().addListener((obs) -> this.refreshWhitelists());
        whitelist3.setOnActionEvent(
                e -> new FilteredMachinePacket(splitter, this.splitter.getFacing().rotateY().getOpposite(),
                        !this.splitter.getWhitelistProperty().get(2)).sendToServer());
        ((GuiButtonSkin) whitelist3.getSkin()).setBackground(new Background((Color) this.whitelistBackground));
        ((GuiButtonSkin) whitelist3.getSkin())
                .setHoveredBackground(new Background((Color) this.whitelistHoveredBackground));
        whitelist3.setWidth(55);
        whitelist3.setHeight(15);
        this.whitelist.add(whitelist3);
    }

    private void refreshSlots(final ItemStack stack, final int filter)
    {
        this.filterPane.get(filter).clearChilds();
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
                    view.setTooltip(true);
                    ((ItemStackViewSkin) view.getSkin()).setBackground(new Background(GuiSplitter.SLOT));
                    this.filterPane.get(filter).addChild(view, 82 + 18 * (i % 5), i > 4 ? 18 : 0);
                }
                this.filterPane.get(filter).addChild(this.whitelist.get(filter), 26, 1);
            }
        }
    }

    private void refreshWhitelists()
    {
        for (int i = 0; i < 3; i++)
        {
            this.whitelist.get(i).setText(this.splitter.getWhitelistProperty().get(i) ? "WHITELIST" : "BLACKLIST");

            if (this.splitter.getWhitelistProperty().get(i))
            {
                ((GuiButtonSkin) this.whitelist.get(i).getSkin()).getBackground().setFill(this.whitelistBackground);
                ((GuiButtonSkin) this.whitelist.get(i).getSkin()).getHoveredBackground()
                        .setFill(this.whitelistHoveredBackground);
                this.whitelist.get(i).setTextColor(Color.fromHex("#000000", 0.87f));
            }
            else
            {
                ((GuiButtonSkin) this.whitelist.get(i).getSkin()).getBackground().setFill(this.blacklistBackground);
                ((GuiButtonSkin) this.whitelist.get(i).getSkin()).getHoveredBackground()
                        .setFill(this.blacklistHoveredBackground);
                this.whitelist.get(i).setTextColor(Color.fromHex("#ffffff", 0.87f));
            }
        }
    }
}
