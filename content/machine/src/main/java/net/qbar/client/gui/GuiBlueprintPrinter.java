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
import org.yggard.brokkgui.control.GuiToggleGroup;
import org.yggard.brokkgui.element.GuiRadioButton;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.skin.GuiBehaviorSkinBase;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.container.ItemStackView;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiBlueprintPrinter extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/blueprintprinter.png",
            0, 0, xSize / 256.0f, ySize / 256.0f);

    private final TileBlueprintPrinter blueprintPrinter;

    private final GuiAbsolutePane            blueprintPane;
    private final GuiAbsolutePane            buttonPane;
    private       BaseProperty<EMachineType> selectedType;

    public GuiBlueprintPrinter(final EntityPlayer player, final TileBlueprintPrinter blueprintPrinter)
    {
        super(blueprintPrinter.createContainer(player));
        this.setWidth(xSize);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.blueprintPrinter = blueprintPrinter;

        GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);

        GuiAbsolutePane body = new GuiAbsolutePane();
        body.setWidthRatio(1);
        body.setHeightRatio(1);
        body.setBackground(new Background(BACKGROUND));
        mainPanel.addChild(body, 0.5f, 0.5f);

        this.blueprintPane = new GuiAbsolutePane();
        this.blueprintPane.setWidth(176);
        this.blueprintPane.setHeight(65);
        body.addChild(this.blueprintPane, 0, 18);

        this.buttonPane = new GuiAbsolutePane();
        this.buttonPane.setWidth(176);
        this.buttonPane.setHeight(18);
        body.addChild(this.buttonPane, 0, 0);

        this.initButtons();
        this.initBlueprints(blueprintPane);

        this.selectedType.addListener((obs, oldValue, newValue) -> this.initBlueprints(blueprintPane));
    }

    private void initButtons()
    {
        this.selectedType = new BaseProperty<>(null, "machineTypeProperty");

        GuiToggleGroup toggleGroup = new GuiToggleGroup();
        toggleGroup.setAllowNothing(true);

        int i = 0;
        for (EMachineType type : EMachineType.values())
        {
            GuiRadioButton button = new GuiRadioButton();
            button.setToggleGroup(toggleGroup);

            this.buttonPane.addChild(button, 7 + 18 * i, 0);
            i++;
        }
        toggleGroup.getSelectedButtonProperty().addListener((obs, oldValue, newValue) -> this.selectedType.setValue(
                newValue != null ? EMachineType.values()[toggleGroup.getButtonList().indexOf(newValue)] : null));
    }

    private void initBlueprints(GuiAbsolutePane mainPane)
    {
        Set<MachineDescriptor> blueprintList = QBarMachines.getAllByComponent(Blueprint.class);

        int maxTier = blueprintList.stream().max(Comparator.comparingInt(descriptor -> descriptor.getTier().ordinal()))
                .get().getTier().ordinal();

        int currentHeight = 7;
        mainPane.clearChilds();
        for (int i = 0; i <= maxTier; i++)
        {
            int currentTier = i;
            long tierElements = blueprintList.stream()
                    .filter(descriptor -> descriptor.getTier().ordinal() == currentTier).count();

            GuiAbsolutePane tierPane = new GuiAbsolutePane();
            tierPane.setWidth(176);
            tierPane.setHeight(18 * (int) Math.ceil(tierElements / 9D));

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
                itemStack.setWidth(18);
                itemStack.setHeight(18);
                itemStack.setTooltip(true);
                itemStack.setAlternateString("");
                ((GuiBehaviorSkinBase) itemStack.getSkin())
                        .setBackground(new Background(machineDescriptor.getType().getColor()));

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

                tierPane.addChild(itemStack, 7 + 18 * (j % 9), 18 * (j / 9));
            }

            mainPane.addChild(tierPane, 0, currentHeight);
            currentHeight += tierPane.getHeight();
        }
    }
}
