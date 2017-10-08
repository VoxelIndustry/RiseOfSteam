package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.CraftCard;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.slot.ListenerSlot;
import net.qbar.common.tile.machine.TileAssembler;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Color;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.shape.Rectangle;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.container.ItemStackView;

public class GuiAssembler extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 188;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/assembler.png", 0, 0,
            xSize / 256.0f, ySize / 256.0f);

    private final TileAssembler assembler;

    final GuiAbsolutePane craftPane;

    public GuiAssembler(final EntityPlayer player, final TileAssembler assembler)
    {
        super(assembler.createContainer(player));
        this.setWidth(xSize);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.assembler = assembler;

        final GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);

        mainPanel.setBackground(new Background(BACKGROUND));

        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(this::refreshCraftSlots);

        this.craftPane = new GuiAbsolutePane();
        this.craftPane.setWidth(104);
        this.craftPane.setHeight(54);
        this.refreshCraftSlots(this.getContainer().getSlot(36).getStack());
        mainPanel.addChild(craftPane, 61, 9);

        Rectangle rectangle = new Rectangle(0, 0, assembler.getProgressScaled(18), 17);
        rectangle.setStyle("-texture: url(" + QBarConstants.MODID + ":textures/gui/assembler.png," + (176 / 256f) +
                "," + (18 / 256f) + "," + ((176 + assembler.getProgressScaled(18)) / 256f) + "," + (35 / 256f));

        this.assembler.getCurrentProgressProperty().addListener(obs ->
        {
            rectangle.setWidth(assembler.getProgressScaled(18));
            rectangle.setStyle("-texture: url(" + QBarConstants.MODID + ":textures/gui/assembler.png," + (176 / 256f)
                    + "," + (18 / 256f) + "," + ((176 + assembler.getProgressScaled(18)) / 256f) + "," + (35 / 256f));
        });
        mainPanel.addChild(rectangle, 115, 10);
    }

    private void refreshCraftSlots(final ItemStack stack)
    {
        this.craftPane.clearChilds();
        if (stack.hasTagCompound())
        {
            final IPunchedCard card = PunchedCardDataManager.getInstance().readFromNBT(stack.getTagCompound());
            if (card != null && card.getID() == PunchedCardDataManager.ECardType.CRAFT.getID())
            {
                for (int i = 0; i < 9; i++)
                {
                    final int index = i;
                    final ItemStackView view = new ItemStackView(((CraftCard) card).recipe[index]);

                    view.setWidth(18);
                    view.setHeight(18);
                    view.setTooltip(true);
                    view.setColor(new Color(1, 1, 1, 0.5f));
                    this.craftPane.addChild(view, 18 * (i % 3), 18 * (i / 3));
                }
                final ItemStackView resultView = new ItemStackView(((CraftCard) card).result);
                resultView.setWidth(18);
                resultView.setHeight(18);
                resultView.setTooltip(true);
                resultView.setColor(new Color(1, 1, 1, 0.5f));
                this.craftPane.addChild(resultView, 72, 0);
            }
        }
    }
}
