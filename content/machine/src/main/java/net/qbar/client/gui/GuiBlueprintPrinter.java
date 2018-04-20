package net.qbar.client.gui;

import fr.ourten.teabeans.value.BaseProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.EMachineType;
import net.qbar.common.machine.MachineDescriptor;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.network.action.ServerActionBuilder;
import net.qbar.common.tile.machine.TileBlueprintPrinter;
import org.yggard.brokkgui.control.GuiToggleButton;
import org.yggard.brokkgui.control.GuiToggleGroup;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.panel.ScrollPane;
import org.yggard.brokkgui.policy.EScrollbarPolicy;
import org.yggard.brokkgui.shape.Rectangle;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.container.ItemStackView;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiBlueprintPrinter extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 222;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/blueprintprinter.png");

    private final TileBlueprintPrinter blueprintPrinter;

    private final GuiAbsolutePane            blueprintPane;
    private final GuiAbsolutePane            buttonPane;
    private       BaseProperty<EMachineType> selectedType;

    public GuiBlueprintPrinter(final EntityPlayer player, final TileBlueprintPrinter blueprintPrinter)
    {
        super(blueprintPrinter.createContainer(player));
        this.setWidth(xSize + 24);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.addStylesheet("/assets/qbar/css/engineer_workshop.css");
        this.addStylesheet("/assets/qbar/css/blueprint_printer.css");

        this.blueprintPrinter = blueprintPrinter;

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);

        GuiAbsolutePane body = new GuiAbsolutePane();
        body.setWidth(xSize);
        body.setHeightRatio(1);
        body.setBackgroundTexture(BACKGROUND);

        mainPanel.addChild(body, 23, 0);
        mainPanel.addChild(new EngineerTabPane(blueprintPrinter, blueprintPrinter.getType()), 0, 0);

        final GuiLabel title = new GuiLabel(blueprintPrinter.getDisplayName().getFormattedText());
        body.addChild(title, 5, 4);

        this.blueprintPane = new GuiAbsolutePane();
        this.blueprintPane.setWidth(120);
        this.blueprintPane.setHeight((QBarMachines.getAll().size() / 5 * 24));
        this.blueprintPane.setID("blueprint");

        ScrollPane scrollPane = new ScrollPane(blueprintPane);
        scrollPane.setWidth(120);
        scrollPane.setHeight(120);
        scrollPane.setFocused(true);
        scrollPane.setScrollYPolicy(EScrollbarPolicy.NEVER);

        body.addChild(scrollPane, 48, 14);

        this.buttonPane = new GuiAbsolutePane();
        this.buttonPane.setWidth(20);
        this.buttonPane.setHeight(19 * EMachineType.values().length);
        body.addChild(this.buttonPane, 27, 14);

        this.initButtons();
        this.initBlueprints(blueprintPane);

        this.getListeners().attach(this.selectedType, (obs, oldValue, newValue) -> this.initBlueprints(blueprintPane));
    }

    private void initButtons()
    {
        this.selectedType = new BaseProperty<>(null, "machineTypeProperty");

        GuiToggleGroup toggleGroup = new GuiToggleGroup();
        toggleGroup.setAllowNothing(true);

        for (int i = 0; i < EMachineType.values().length; i++)
        {
            GuiToggleButton button = new GuiToggleButton();
            button.setWidth(20);
            button.setHeight(19);
            button.setToggleGroup(toggleGroup);
            button.addStyleClass("category");

            this.buttonPane.addChild(button, 0, i * 19);
        }
        this.getListeners().attach(toggleGroup.getSelectedButtonProperty(),
                (obs, oldValue, newValue) -> this.selectedType.setValue(newValue != null ?
                        EMachineType.values()[toggleGroup.getButtonList().indexOf(newValue)] : null));
    }

    private void initBlueprints(GuiAbsolutePane mainPane)
    {
        Set<MachineDescriptor> blueprintList = QBarMachines.getAllByComponent(Blueprint.class);

        int maxTier = blueprintList.stream().max(Comparator.comparingInt(descriptor -> descriptor.getTier().ordinal()))
                .get().getTier().ordinal();

        int currentHeight = 0;
        mainPane.clearChilds();
        for (int i = 0; i <= maxTier; i++)
        {
            int currentTier = i;
            long tierElements = blueprintList.stream()
                    .filter(descriptor -> descriptor.getTier().ordinal() == currentTier).count();

            GuiAbsolutePane tierPane = new GuiAbsolutePane();
            tierPane.setWidth(120);
            tierPane.setHeight(10 + 24 * (int) Math.ceil(tierElements / 5D) + (currentTier != maxTier ? 5 : 0));
            tierPane.addChild(new GuiLabel("Tier " + currentTier), 3, 1);

            if (currentTier != maxTier)
            {
                Rectangle divider = new Rectangle();
                divider.setStyle("-color: #FFFFFF 48%;");
                divider.setWidth(116);
                divider.setHeight(2);
                tierPane.addChild(divider, 2, tierPane.getHeight() - 5);
            }

            List<MachineDescriptor> collect;
            if (this.selectedType.getValue() == null)
                collect = blueprintList.stream().filter(descriptor -> descriptor.getTier().ordinal() == currentTier)
                        .sorted(Comparator.comparing(MachineDescriptor::getType)).collect(Collectors.toList());
            else
                collect = blueprintList.stream().filter(descriptor -> descriptor.getTier().ordinal() == currentTier
                        && descriptor.getType() == this.selectedType.getValue()).collect(Collectors.toList());
            for (int j = 0; j < collect.size(); j++)
            {
                MachineDescriptor machineDescriptor = collect.get(j);
                ItemStackView itemStack = new ItemStackView(
                        new ItemStack(Item.getByNameOrId(QBarConstants.MODID + ":" + machineDescriptor.getName())));
                itemStack.setWidth(24);
                itemStack.setHeight(24);
                itemStack.setTooltip(true);
                itemStack.setAlternateString("");
                itemStack.setBackgroundColor(machineDescriptor.getType().getColor().addAlpha(-0.4f));

                itemStack.setOnClickEvent(e ->
                {
                    new ServerActionBuilder("PRINT").toTile(this.blueprintPrinter)
                            .withString("blueprint", machineDescriptor.getName()).then(response ->
                    {
                        if (!response.hasKey("blueprint"))
                            return;
                        ItemStack blueprint = new ItemStack(QBarItems.BLUEPRINT);
                        NBTTagCompound tag = new NBTTagCompound();
                        blueprint.setTagCompound(tag);

                        tag.setString("blueprint", machineDescriptor.getName());

                        Minecraft.getMinecraft().player.inventory.setItemStack(blueprint);
                    }).send();
                });

                tierPane.addChild(itemStack, 24 * (j % 5), 10 + 24 * (j / 5));
            }

            mainPane.addChild(tierPane, 0, currentHeight);
            currentHeight += tierPane.getHeight();
        }
        blueprintPane.setHeight(currentHeight);
    }
}
