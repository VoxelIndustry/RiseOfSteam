package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.FilterCard;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.card.PunchedCardDataManager.ECardType;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.slot.ListenerSlot;
import net.qbar.common.network.action.ServerActionBuilder;
import net.qbar.common.tile.machine.TileSplitter;
import org.yggard.brokkgui.data.EAlignment;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.container.ItemStackView;

import java.util.ArrayList;
import java.util.List;

public class GuiSplitter extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 206;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/splitter.png", 0, 0,
            GuiSplitter.xSize / 256.0f, GuiSplitter.ySize / 256.0f);
    private static final Texture SLOT       = new Texture(QBarConstants.MODID + ":textures/gui/slot.png", 0, 0, 1, 1);

    private final TileSplitter splitter;

    private final List<GuiAbsolutePane> filterPane;
    private final List<GuiButton>       whitelist;

    public GuiSplitter(final EntityPlayer player, final TileSplitter splitter)
    {
        super(splitter.createContainer(player));
        this.setWidth(GuiSplitter.xSize);
        this.setHeight(GuiSplitter.ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.addStylesheet("/assets/qbar/css/splitter.css");
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

        this.whitelist = new ArrayList<>();

        final GuiButton whitelist1 = new GuiButton("WHITELIST");
        whitelist1.setOnActionEvent(e -> new ServerActionBuilder("WHITELIST").toTile(splitter)
                .withInt("facing", this.splitter.getFacing().rotateY().ordinal())
                .withBoolean("whitelist", !this.splitter.getWhitelistProperty().get(0)).send());
        whitelist1.setWidth(55);
        whitelist1.setHeight(15);
        this.whitelist.add(whitelist1);

        final GuiButton whitelist2 = new GuiButton("WHITELIST");
        whitelist2.setOnActionEvent(e -> new ServerActionBuilder("WHITELIST").toTile(splitter)
                .withInt("facing", this.splitter.getFacing().getOpposite().ordinal())
                .withBoolean("whitelist", !this.splitter.getWhitelistProperty().get(1)).send());
        whitelist2.setWidth(55);
        whitelist2.setHeight(15);
        this.whitelist.add(whitelist2);

        final GuiButton whitelist3 = new GuiButton("WHITELIST");
        whitelist3.setOnActionEvent(e -> new ServerActionBuilder("WHITELIST").toTile(splitter)
                .withInt("facing", this.splitter.getFacing().rotateYCCW().ordinal())
                .withBoolean("whitelist", !this.splitter.getWhitelistProperty().get(2)).send());
        whitelist3.setWidth(55);
        whitelist3.setHeight(15);
        this.whitelist.add(whitelist3);

        this.splitter.getWhitelistProperty().addListener(obs -> this.refreshWhitelists());
        this.refreshWhitelists();
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
                    view.setStyle("-background-texture: url(\"" + QBarConstants.MODID + ":textures/gui/slot.png\");");
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
            if (this.splitter.getWhitelistProperty().get(i))
            {
                this.whitelist.get(i).setText("WHITELIST");
                this.whitelist.get(i).getStyleClass().replace("blacklist", "whitelist");
            }
            else
            {
                this.whitelist.get(i).setText("BLACKLIST");
                this.whitelist.get(i).getStyleClass().replace("whitelist", "blacklist");
            }
            this.whitelist.get(i).refreshStyle();
        }
    }
}
