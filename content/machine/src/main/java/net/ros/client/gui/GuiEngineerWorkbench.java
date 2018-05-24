package net.ros.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.ros.common.ROSConstants;
import net.ros.common.container.BuiltContainer;
import net.ros.common.network.action.ServerActionBuilder;
import net.ros.common.tile.machine.TileEngineerWorkbench;
import org.lwjgl.input.Keyboard;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;
import org.yggard.brokkgui.wrapper.elements.ItemStackView;

public class GuiEngineerWorkbench extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 204;

    private static final Texture BACKGROUND = new Texture(ROSConstants.MODID + ":textures/gui/engineerworkbench.png",
            0, 0,
            xSize / 256.0f, ySize / 256.0f);

    private final TileEngineerWorkbench engineerWorkbench;

    private GuiAbsolutePane viewPanel = new GuiAbsolutePane();

    public GuiEngineerWorkbench(final EntityPlayer player, final TileEngineerWorkbench engineerWorkbench)
    {
        super(engineerWorkbench.createContainer(player));

        this.setWidth(xSize + 24);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.addStylesheet("/assets/ros/css/engineer_workshop.css");

        this.engineerWorkbench = engineerWorkbench;

        GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);

        GuiAbsolutePane body = new GuiAbsolutePane();
        body.setWidth(xSize);
        body.setHeightRatio(1);
        body.setBackgroundTexture(BACKGROUND);

        mainPanel.addChild(body, 23, 0);
        mainPanel.addChild(new EngineerTabPane(engineerWorkbench, engineerWorkbench.getType()), 0, 0);

        viewPanel.setWidth(162);
        viewPanel.setHeight(108);
        body.addChild(viewPanel, 7, 8);

        this.getListeners().attach(engineerWorkbench.getCraftablesDirty(), (obs, oldValue, newValue) ->
        {
            if (newValue)
                this.rebuildViews();
        });
        this.rebuildViews();
    }

    private void rebuildViews()
    {
        viewPanel.clearChilds();

        int index = 0;
        for (ItemStack stack : engineerWorkbench.getCraftables())
        {
            ItemStack copy = stack.copy();
            copy.setCount(1);

            ItemStackView view = new ItemStackView(copy);
            view.setAlternateString(engineerWorkbench.getCraftablesCount()[index] == 0 ? TextFormatting.RED + "0" :
                    String.valueOf(engineerWorkbench.getCraftablesCount()[index]));
            view.setWidth(18);
            view.setHeight(18);
            view.setItemTooltip(true);

            int finalIndex = index;
            view.setOnClickEvent(e ->
            {
                new ServerActionBuilder("CRAFT_ITEM")
                        .withInt("index", finalIndex)
                        .withBoolean("stack", Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ||
                                Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
                        .toTile(engineerWorkbench)
                        .then(response -> Minecraft.getMinecraft().player.inventory.setItemStack(
                                new ItemStack(response.getCompoundTag("cursor"))))
                        .send();
            });

            viewPanel.addChild(view, 18 * (index % 9), 18 * (index / 9));
            index++;
        }
        this.engineerWorkbench.getCraftablesDirty().setValue(false);
    }
}
