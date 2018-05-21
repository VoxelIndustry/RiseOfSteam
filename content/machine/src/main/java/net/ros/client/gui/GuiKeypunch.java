package net.ros.client.gui;

import fr.ourten.teabeans.listener.ListValueChangeListener;
import fr.ourten.teabeans.listener.ValueInvalidationListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.ros.common.ROSConstants;
import net.ros.common.card.CardDataStorage;
import net.ros.common.card.CraftCard;
import net.ros.common.card.FilterCard;
import net.ros.common.container.BuiltContainer;
import net.ros.common.init.ROSItems;
import net.ros.common.network.action.ServerActionBuilder;
import net.ros.common.util.ItemUtils;
import net.ros.common.container.slot.ListenerSlot;
import net.ros.common.tile.machine.TileKeypunch;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.container.ItemStackView;

public class GuiKeypunch extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(ROSConstants.MODID + ":textures/gui/keypunch.png", 0, 0,
            GuiKeypunch.xSize / 256.0f, GuiKeypunch.ySize / 256.0f);
    private static final Texture SLOT       = new Texture(ROSConstants.MODID + ":textures/gui/slot.png", 0, 0, 1, 1);

    private final TileKeypunch keypunch;

    private final GuiRelativePane header, body;

    private final GuiButton assemble;

    final GuiAbsolutePane filterPane, craftPane;

    public GuiKeypunch(final EntityPlayer player, final TileKeypunch keypunch)
    {
        super(keypunch.createContainer(player));
        this.setWidth(GuiKeypunch.xSize + 24);
        this.setHeight(GuiKeypunch.ySize + 18);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.keypunch = keypunch;

        final GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);

        this.header = new GuiRelativePane();
        this.header.setWidth(xSize);
        this.header.setHeight(18);

        this.body = new GuiRelativePane();
        this.body.setWidth(xSize);
        this.body.setHeight(ySize);
        this.body.setBackgroundTexture(GuiKeypunch.BACKGROUND);

        mainPanel.addChild(this.header, 23, 0);
        mainPanel.addChild(this.body, 23, 18);
        mainPanel.addChild(new EngineerTabPane(keypunch, keypunch.getType()), 0, 18);

        this.craftPane = new GuiAbsolutePane();
        this.filterPane = new GuiAbsolutePane();
        this.initPanels(player);

        final GuiButton craftTab = new GuiButton("CRAFT");
        final GuiButton filterTab = new GuiButton("FILTER");

        this.getListeners().attach(this.keypunch.getCraftTabProperty(), (obs) ->
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
            this.refreshMessage(this.getContainer().getSlot(36).getStack());
        });

        craftTab.addStyleClass("tab-button");
        craftTab.setWidthRatio(0.5f);
        craftTab.setHeightRatio(1);
        craftTab.setOnActionEvent(e ->
        {
            this.keypunch.getCraftTabProperty().setValue(true);
            new ServerActionBuilder("SET_TAB").toTile(keypunch).withInt("tab", 0).send();
        });

        filterTab.addStyleClass("tab-button");
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
        this.assemble.setID("assemble");
        this.assemble.setWidth(56);
        this.assemble.setHeight(16);
        this.assemble.setOnActionEvent(e ->
                new ServerActionBuilder(this.assemble.getLabel().getText().equals("PRINT") ? "PRINT_CARD" : "LOAD_CARD")
                        .toTile(keypunch).send());
        this.body.addChild(this.assemble, 0.5f, 0.415f);

        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(this::refreshMessage);
        ((ListenerSlot) this.getContainer().getSlot(37))
                .setOnChange(stack -> this.refreshMessage(this.getContainer().getSlot(36).getStack()));

        ValueInvalidationListener refreshMessage = obs -> this.refreshMessage(this.getContainer().getSlot(36)
                .getStack());
        this.getListeners().attach(this.keypunch.getCraftStacks(), refreshMessage);
        this.getListeners().attach(this.keypunch.getFilterStacks(), refreshMessage);
        this.refreshMessage(this.getContainer().getSlot(36).getStack());

        this.addStylesheet("/assets/ros/css/keypunch.css");
        this.addStylesheet("/assets/ros/css/engineer_workshop.css");
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
            view.setBackgroundTexture(GuiKeypunch.SLOT);
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
        resultView.setBackgroundTexture(GuiKeypunch.SLOT);
        this.craftPane.addChild(resultView, 25 + (18 * 4), 3 + 18);

        this.getListeners().attach(this.keypunch.getCraftStacks(),
                (ListValueChangeListener<ItemStack>) (obs, oldStack, currentStack) ->
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
            view.setBackgroundTexture(GuiKeypunch.SLOT);
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

        this.getListeners().attach(this.keypunch.getFilterStacks(), obs ->
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
            if (stack.getTagCompound() == null)
            {
                this.assemble.getLabel().setText("PRINT");

                if (this.keypunch.getCanPrintProperty().getValue())
                {
                    if (this.getContainer().getSlot(37).getStack().isEmpty())
                        this.assemble.setDisabled(false);
                    else
                    {
                        final ItemStack temp = new ItemStack(ROSItems.PUNCHED_CARD, 1, 1);
                        temp.setTagCompound(new NBTTagCompound());

                        if (this.keypunch.getCraftTabProperty().getValue())
                        {
                            final CraftCard card = new CraftCard(CardDataStorage.ECardType.CRAFT.getID());
                            for (int i = 0; i < this.keypunch.getCraftStacks().size(); i++)
                                card.setIngredient(i, this.keypunch.getCraftStacks().get(i));
                            card.setResult(this.keypunch.getRecipeResult());
                            CardDataStorage.instance().write(temp.getTagCompound(), card);
                        }
                        else
                        {
                            final FilterCard card = new FilterCard(CardDataStorage.ECardType.FILTER.getID());
                            for (int i = 0; i < this.keypunch.getFilterStacks().size(); i++)
                                card.stacks[i] = this.keypunch.getFilterStacks().get(i);
                            CardDataStorage.instance().write(temp.getTagCompound(), card);
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
                this.assemble.getLabel().setText("LOAD");
                if (this.keypunch.getCraftTabProperty().getValue()
                        && stack.getTagCompound().getInteger("cardTypeID") == CardDataStorage.ECardType.CRAFT
                        .getID()
                        || !this.keypunch.getCraftTabProperty().getValue() && stack.getTagCompound()
                        .getInteger("cardTypeID") == CardDataStorage.ECardType.FILTER.getID())
                    this.assemble.setDisabled(false);
                else
                    this.assemble.setDisabled(true);
            }
        }
        else
            this.assemble.setDisabled(true);
    }
}
