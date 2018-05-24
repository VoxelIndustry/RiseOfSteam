package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.ros.common.ROSConstants;
import net.ros.common.card.CardDataStorage;
import net.ros.common.card.CraftCard;
import net.ros.common.card.IPunchedCard;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.slot.ListenerSlot;
import net.ros.common.tile.machine.TileAssembler;
import org.yggard.brokkgui.paint.Color;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.shape.Rectangle;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.elements.ItemStackView;

public class GuiAssembler extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 188;

    private static final Texture BACKGROUND = new Texture(ROSConstants.MODID + ":textures/gui/assembler.png", 0, 0,
            xSize / 256.0f, ySize / 256.0f);

    private final TileAssembler assembler;

    private final GuiAbsolutePane craftPane;
    private final ItemStackView[] ingredients = new ItemStackView[9];
    private final ItemStackView   result;

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

        mainPanel.setBackgroundTexture(BACKGROUND);

        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(this::refreshCraftSlots);

        this.craftPane = new GuiAbsolutePane();
        this.craftPane.setWidth(104);
        this.craftPane.setHeight(54);
        mainPanel.addChild(craftPane, 61, 9);

        Rectangle rectangle = new Rectangle(0, 0, assembler.getProgressScaled(18), 17);
        rectangle.setStyle("-texture: url(" + ROSConstants.MODID + ":textures/gui/assembler.png," + (176 / 256f) +
                "," + (18 / 256f) + "," + ((176 + assembler.getProgressScaled(18)) / 256f) + "," + (35 / 256f));

        this.getListeners().attach(assembler.getCurrentProgressProperty(), obs ->
        {
            rectangle.setWidth(assembler.getProgressScaled(18));
            rectangle.setStyle("-texture: url(" + ROSConstants.MODID + ":textures/gui/assembler.png," + (176 / 256f)
                    + "," + (18 / 256f) + "," + ((176 + assembler.getProgressScaled(18)) / 256f) + "," + (35 / 256f));
        });
        mainPanel.addChild(rectangle, 115, 10);

        for (int i = 0; i < 9; i++)
        {
            ItemStackView ingredient = new ItemStackView();
            ingredient.setWidth(18);
            ingredient.setHeight(18);
            ingredient.setItemTooltip(true);
            ingredient.setColor(new Color(1, 1, 1, 0.5f));
            this.craftPane.addChild(ingredient, 18 * (i % 3), 18 * (i / 3));
            this.ingredients[i] = ingredient;

            ((ListenerSlot) this.getContainer().getSlot(57 + i)).setOnChange(stack ->
                    ingredient.setVisible(stack.isEmpty()));
        }

        ItemStackView result = new ItemStackView();
        result.setWidth(18);
        result.setHeight(18);
        result.setItemTooltip(true);
        result.setColor(new Color(1, 1, 1, 0.5f));
        this.craftPane.addChild(result, 72, 0);
        this.result = result;

        ((ListenerSlot) this.getContainer().getSlot(47)).setOnChange(stack ->
                result.setVisible(stack.isEmpty()));

        this.refreshCraftSlots(this.getContainer().getSlot(36).getStack());
    }

    private void refreshCraftSlots(final ItemStack stack)
    {
        boolean stackEmpty = false;

        if (stack.hasTagCompound())
        {
            final IPunchedCard card = CardDataStorage.instance().read(stack.getTagCompound());
            if (card != null && card.getID() == CardDataStorage.ECardType.CRAFT.getID())
            {
                for (int i = 0; i < 9; i++)
                    this.ingredients[i].setItemStack(((CraftCard) card).getRecipe()[i]);
                this.result.setItemStack(((CraftCard) card).getResult());
            }
            else
                stackEmpty = true;
        }
        else
            stackEmpty = true;

        if (stackEmpty)
        {
            for (int i = 0; i < 9; i++)
                this.ingredients[i].setItemStack(ItemStack.EMPTY);
            this.result.setItemStack(ItemStack.EMPTY);
        }
    }
}
