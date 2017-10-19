package net.qbar.client.gui;

import fr.ourten.teabeans.listener.ListValueChangeListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.CraftCard;
import net.qbar.common.card.FilterCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.slot.ListenerSlot;
import net.qbar.common.init.QBarItems;
import net.qbar.common.network.action.ServerActionBuilder;
import net.qbar.common.tile.machine.TileKeypunch;
import net.qbar.common.util.ItemUtils;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Color;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.skin.GuiButtonSkin;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.container.ItemStackView;
import org.yggard.brokkgui.wrapper.container.ItemStackViewSkin;

public class GuiKeypunch extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/keypunch.png", 0, 0,
            GuiKeypunch.xSize / 256.0f, GuiKeypunch.ySize / 256.0f);
    private static final Texture SLOT       = new Texture(QBarConstants.MODID + ":textures/gui/slot.png", 0, 0, 1, 1);

    private final TileKeypunch keypunch;

    private final GuiRelativePane header, body;

    private final GuiButton assemble;

    final GuiAbsolutePane filterPane, craftPane;

    public GuiKeypunch(final EntityPlayer player, final TileKeypunch keypunch)
    {
        super(keypunch.createContainer(player));
        this.setWidth(GuiKeypunch.xSize);
        this.setHeight(GuiKeypunch.ySize + 18);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.keypunch = keypunch;

        final GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);

        this.header = new GuiRelativePane();
        this.header.setWidthRatio(1);
        this.header.setHeightRatio(0.1f);
        mainPanel.addChild(this.header, 0.5f, 0.05f);

        this.body = new GuiRelativePane();
        this.body.setWidthRatio(1);
        this.body.setHeightRatio(0.9f);
        this.body.setBackground(new Background(GuiKeypunch.BACKGROUND));
        mainPanel.addChild(this.body, 0.5f, 0.55f);

        this.craftPane = new GuiAbsolutePane();
        this.filterPane = new GuiAbsolutePane();
        this.initPanels(player);

        final GuiButton craftTab = new GuiButton("CRAFT");
        final GuiButton filterTab = new GuiButton("FILTER");

        this.keypunch.getCraftTabProperty().addListener((obs) ->
        {
            craftTab.setDisabled(this.keypunch.getCraftTabProperty().getValue());
            filterTab.setDisabled(!this.keypunch.getCraftTabProperty().getValue());

            if (this.keypunch.getCraftTabProperty().getValue())
            {
                if (!this.body.hasChild(this.craftPane))
                    this.body.addChild(this.craftPane, 0.5f, 0.18f);
                if (this.body.hasChild(this.filterPane))
                    this.body.removeChild(this.filterPane);
            }
            else
            {
                if (!this.body.hasChild(this.filterPane))
                    this.body.addChild(this.filterPane, 0.5f, 0.18f);
                if (this.body.hasChild(this.craftPane))
                    this.body.removeChild(this.craftPane);
            }
        });

        ((GuiButtonSkin) craftTab.getSkin()).setBackground(new Background(Color.fromHex("#9E9E9E", 0.12f)));
        ((GuiButtonSkin) craftTab.getSkin()).setHoveredBackground(new Background(Color.fromHex("#BDBDBD", 0.5f)));
        ((GuiButtonSkin) craftTab.getSkin()).setDisabledBackground(new Background(Color.fromHex("#9E9E9E")));
        craftTab.setWidthRatio(0.5f);
        craftTab.setHeightRatio(1);
        craftTab.setOnActionEvent(e ->
        {
            this.keypunch.getCraftTabProperty().setValue(true);
            new ServerActionBuilder("SET_TAB").toTile(keypunch).withInt("tab", 0).send();
        });

        ((GuiButtonSkin) filterTab.getSkin()).setBackground(new Background(Color.fromHex("#9E9E9E", 0.12f)));
        ((GuiButtonSkin) filterTab.getSkin()).setHoveredBackground(new Background(Color.fromHex("#BDBDBD", 0.5f)));
        ((GuiButtonSkin) filterTab.getSkin()).setDisabledBackground(new Background(Color.fromHex("#9E9E9E")));
        filterTab.setWidthRatio(0.5f);
        filterTab.setHeightRatio(1);
        filterTab.setOnActionEvent(e ->
        {
            this.keypunch.getCraftTabProperty().setValue(false);
            new ServerActionBuilder("SET_TAB").toTile(keypunch).withInt("tab", 1).send();
        });

        this.header.addChild(craftTab, 0.25f, 0.5f);
        this.header.addChild(filterTab, 0.75f, 0.5f);

        this.assemble = new GuiButton("PRINT");
        this.assemble.setWidth(56);
        this.assemble.setHeight(16);
        this.assemble.setOnActionEvent(e ->
                new ServerActionBuilder(this.assemble.getText().equals("PRINT") ? "PRINT_CARD" : "LOAD_CARD")
                        .toTile(keypunch).send());

        ((GuiButtonSkin) this.assemble.getSkin()).setBackground(new Background(Color.fromHex("#03A9F4")));
        ((GuiButtonSkin) this.assemble.getSkin()).setHoveredBackground(new Background(Color.fromHex("#4FC3F7")));
        ((GuiButtonSkin) this.assemble.getSkin()).setDisabledBackground(new Background(Color.fromHex("#9E9E9E", 0.5f)));

        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(this::refreshMessage);
        ((ListenerSlot) this.getContainer().getSlot(37))
                .setOnChange(stack -> this.refreshMessage(this.getContainer().getSlot(36).getStack()));
        this.keypunch.getCanPrintProperty()
                .addListener(obs -> this.refreshMessage(this.getContainer().getSlot(36).getStack()));
        this.keypunch.getFilterStacks()
                .addListener(obs -> this.refreshMessage(this.getContainer().getSlot(36).getStack()));
        this.refreshMessage(this.getContainer().getSlot(36).getStack());
    }

    public void initPanels(final EntityPlayer player)
    {
        this.craftPane.setWidthRatio(1);
        this.craftPane.setHeightRatio(0.36f);

        this.filterPane.setWidthRatio(1);
        this.filterPane.setHeightRatio(0.36f);

        this.refreshCraftSlots(player);
        this.refreshFilterSlots(player);
    }

    private void refreshCraftSlots(final EntityPlayer player)
    {
        this.craftPane.clearChilds();
        for (int i = 0; i < 9; i++)
        {
            final int index = i;
            final ItemStackView view = new ItemStackView(this.keypunch.getCraftStacks().get(index));
            view.setWidth(18);
            view.setHeight(18);
            view.setTooltip(true);
            ((ItemStackViewSkin) view.getSkin()).setBackground(new Background(GuiKeypunch.SLOT));
            view.setOnClickEvent(click ->
            {
                if (click.getKey() == 1)
                {
                    this.keypunch.getCraftStacks().set(index, ItemStack.EMPTY);
                    new ServerActionBuilder("SET_STACK").toTile(keypunch).withInt("slot", index)
                            .withItemStack("itemStack", ItemStack.EMPTY).send();
                }
                else
                {
                    if (!player.inventory.getItemStack().isEmpty())
                    {
                        final ItemStack copy = player.inventory.getItemStack().copy();
                        copy.setCount(1);
                        this.keypunch.getCraftStacks().set(index, copy);
                        new ServerActionBuilder("SET_STACK").toTile(keypunch).withInt("slot", index)
                                .withItemStack("itemStack", copy).send();
                    }
                }
            });
            this.craftPane.addChild(view, 25 + 18 * (i % 3), 3 + 18 * (i / 3));
        }

        final InventoryCrafting fakeInv = new InventoryCrafting(this.getContainer(), 3, 3);
        for (int i = 0; i < 9; i++)
            fakeInv.setInventorySlotContents(i, this.keypunch.getCraftStacks().get(i));

        IRecipe recipe = CraftingManager.findMatchingRecipe(fakeInv, this.keypunch.getWorld());
        final ItemStackView resultView = new ItemStackView(recipe != null ? recipe.getRecipeOutput() : ItemStack.EMPTY);
        resultView.setWidth(22);
        resultView.setHeight(22);
        resultView.setTooltip(true);
        ((ItemStackViewSkin) resultView.getSkin()).setBackground(new Background(GuiKeypunch.SLOT));
        this.craftPane.addChild(resultView, 25 + (18 * 4), 3 + 18);

        this.keypunch.getCraftStacks()
                .addListener((ListValueChangeListener<? super ItemStack>) (obs, oldStack, currentStack) ->
                {
                    if ((oldStack == null && currentStack == null) || (oldStack != null && currentStack != null
                            && ItemUtils.deepEquals(oldStack, currentStack)))
                        return;
                    for (int i = 0; i < 9; i++)
                        ((ItemStackView) this.craftPane.getChildrens().get(i))
                                .setItemStack(this.keypunch.getCraftStacks().get(i));

                    for (int i = 0; i < 9; i++)
                        fakeInv.setInventorySlotContents(i, this.keypunch.getCraftStacks().get(i));

                    IRecipe output = CraftingManager.findMatchingRecipe(fakeInv, this.keypunch.getWorld());
                    resultView.setItemStack(output != null ? output.getRecipeOutput() : ItemStack.EMPTY);
                });
    }

    private void refreshFilterSlots(final EntityPlayer player)
    {
        this.filterPane.clearChilds();
        for (int i = 0; i < 9; i++)
        {
            final int index = i;
            final ItemStackView view = new ItemStackView(this.keypunch.getFilterStacks().get(index));
            view.setWidth(18);
            view.setHeight(18);
            view.setTooltip(true);
            ((ItemStackViewSkin) view.getSkin()).setBackground(new Background(GuiKeypunch.SLOT));
            view.setOnClickEvent(click ->
            {
                if (click.getKey() == 1)
                {
                    this.keypunch.getFilterStacks().set(index, ItemStack.EMPTY);
                    new ServerActionBuilder("SET_STACK").toTile(keypunch).withInt("slot", index)
                            .withItemStack("itemStack", ItemStack.EMPTY).send();
                }
                else
                {
                    if (!player.inventory.getItemStack().isEmpty())
                    {
                        final ItemStack copy = player.inventory.getItemStack().copy();
                        copy.setCount(1);
                        this.keypunch.getFilterStacks().set(index, copy);
                        new ServerActionBuilder("SET_STACK").toTile(keypunch).withInt("slot", index)
                                .withItemStack("itemStack", copy).send();
                    }
                }
            });
            this.filterPane.addChild(view, 25 + 18 * (i % 3), 3 + 18 * (i / 3));
        }
        this.keypunch.getFilterStacks().addListener(obs ->
        {
            for (int i = 0; i < 9; i++)
                ((ItemStackView) this.filterPane.getChildrens().get(i))
                        .setItemStack(this.keypunch.getFilterStacks().get(i));
        });
    }

    private void refreshMessage(final ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            if (!this.body.hasChild(this.assemble))
                this.body.addChild(this.assemble, 0.5f, 0.415f);
            if (stack.getTagCompound() == null)
            {
                this.assemble.setText("PRINT");

                if (this.keypunch.getCanPrintProperty().getValue())
                {
                    if (this.getContainer().getSlot(37).getStack().isEmpty())
                        this.assemble.setDisabled(false);
                    else
                    {
                        final ItemStack temp = new ItemStack(QBarItems.PUNCHED_CARD, 1, 1);
                        temp.setTagCompound(new NBTTagCompound());

                        if (this.keypunch.getCraftTabProperty().getValue())
                        {
                            final CraftCard card = new CraftCard(PunchedCardDataManager.ECardType.CRAFT.getID());
                            for (int i = 0; i < this.keypunch.getCraftStacks().size(); i++)
                                card.recipe[i] = this.keypunch.getCraftStacks().get(i);
                            card.result = this.keypunch.getRecipeResult();
                            PunchedCardDataManager.getInstance().writeToNBT(temp.getTagCompound(), card);
                        }
                        else
                        {
                            final FilterCard card = new FilterCard(PunchedCardDataManager.ECardType.FILTER.getID());
                            for (int i = 0; i < this.keypunch.getFilterStacks().size(); i++)
                                card.stacks[i] = this.keypunch.getFilterStacks().get(i);
                            PunchedCardDataManager.getInstance().writeToNBT(temp.getTagCompound(), card);
                        }
                        this.assemble.setDisabled(
                                !ItemUtils.canMergeStacks(temp, this.getContainer().getSlot(37).getStack()));
                    }
                }
                else
                    this.assemble.setDisabled(true);
            }
            else
            {
                this.assemble.setText("LOAD");
                if (this.keypunch.getCraftTabProperty().getValue()
                        && stack.getTagCompound().getInteger("cardTypeID") == PunchedCardDataManager.ECardType.CRAFT
                        .getID()
                        || !this.keypunch.getCraftTabProperty().getValue() && stack.getTagCompound()
                        .getInteger("cardTypeID") == PunchedCardDataManager.ECardType.FILTER.getID())
                    this.assemble.setDisabled(false);
                else
                    this.assemble.setDisabled(true);
            }
        }
        else if (stack.isEmpty() && this.body.hasChild(this.assemble))
            this.body.removeChild(this.assemble);
    }
}
