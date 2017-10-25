package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.CraftCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.slot.ListenerSlot;
import net.qbar.common.tile.machine.TileCraftCardLibrary;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.container.ItemStackView;

public class GuiCraftCardLibrary extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/craftcardlibrary.png",
            0, 0,
            xSize / 256.0f, ySize / 256.0f);

    private final TileCraftCardLibrary craftCardLibrary;

    private ItemStackView[][] views;
    private CraftCard[][]     cards;

    public GuiCraftCardLibrary(final EntityPlayer player, final TileCraftCardLibrary craftCardLibrary)
    {
        super(craftCardLibrary.createContainer(player));
        this.setWidth(xSize);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.craftCardLibrary = craftCardLibrary;

        GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);

        GuiAbsolutePane body = new GuiAbsolutePane();
        body.setWidthRatio(1);
        body.setHeightRatio(1);
        body.setBackground(new Background(BACKGROUND));
        mainPanel.addChild(body, 0.5f, 0.5f);

        this.setupViews(body);
    }

    private void setupViews(GuiAbsolutePane body)
    {
        this.views = new ItemStackView[8][];
        this.cards = new CraftCard[8][];

        for (int x = 0; x < 8; x++)
        {
            this.views[x] = new ItemStackView[8];
            this.cards[x] = new CraftCard[8];
            for (int y = 0; y < 8; y++)
            {
                this.setupView(body, x, y);
                int finalX = x;
                int finalY = y;
                ((ListenerSlot) this.getContainer().getSlot(y * 8 + x)).setOnChange(stack ->
                        this.setupView(body, finalX, finalY));
            }
        }
    }

    private void setupView(GuiAbsolutePane body, int x, int y)
    {
        ItemStack card = this.getContainer().inventorySlots.get(y * 8 + x).getStack();

        if (card.hasTagCompound() && card.getTagCompound().hasKey("cardTypeID"))
        {
            this.cards[x][y] = ((CraftCard) PunchedCardDataManager.getInstance()
                    .readFromNBT(card.getTagCompound()));
            this.views[x][y] = new ItemStackView(this.cards[x][y].result);
            this.views[x][y].setWidth(18);
            this.views[x][y].setHeight(18);
            this.views[x][y].setTooltip(true);
            this.views[x][y].setzLevel(250);
            body.addChild(this.views[x][y], x * 18, y * 18);
        }
    }
}
