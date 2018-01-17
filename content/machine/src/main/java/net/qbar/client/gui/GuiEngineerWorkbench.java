package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.grid.WorkshopMachine;
import net.qbar.common.network.action.ServerActionBuilder;
import net.qbar.common.tile.machine.TileEngineerWorkbench;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiEngineerWorkbench extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 204;

    private static final Texture BACKGROUND = new Texture(QBarConstants.MODID + ":textures/gui/engineerworkbench.png",
            0, 0,
            xSize / 256.0f, ySize / 256.0f);

    private final TileEngineerWorkbench engineerWorkbench;

    private final GuiRelativePane headerPanel;

    public GuiEngineerWorkbench(final EntityPlayer player, final TileEngineerWorkbench engineerWorkbench)
    {
        super(engineerWorkbench.createContainer(player));

        this.setWidth(xSize);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.engineerWorkbench = engineerWorkbench;

        GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);

        this.headerPanel = new GuiRelativePane();
        headerPanel.setWidthRatio(1);
        headerPanel.setHeightRatio(0.1f);
        this.headerPanel.setStyle("-border-thin: 1; -border-color: green;");
        mainPanel.addChild(headerPanel, 0.5f, 0.05f);

        mainPanel.setStyle("-border-color: pink; -border-thin: 2;");

        GuiAbsolutePane body = new GuiAbsolutePane();
        body.setWidthRatio(1);
        body.setHeightRatio(0.9f);
        body.setBackground(new Background(BACKGROUND));

        mainPanel.addChild(body, 0.5f, 0.55f);

        new ServerActionBuilder("MACHINES_LOAD").toTile(engineerWorkbench).then(response ->
        {
            for (WorkshopMachine machine : WorkshopMachine.VALUES)
            {
                if (response.hasKey(machine.name()))
                    this.addOnglet(machine, BlockPos.fromLong(response.getLong(machine.name())));
            }
        }).send();
    }

    private void addOnglet(WorkshopMachine machine, BlockPos pos)
    {
        GuiButton button = new GuiButton(machine.name());
        button.setWidthRatio(1f / WorkshopMachine.VALUES.length);
        button.setHeightRatio(1);
        button.setStyle("-background-color: aqua; -border-thin: 1; -border-color: red;");

        this.headerPanel.addChild(button, 1f / WorkshopMachine.VALUES.length * machine.ordinal(), 0.5f);
    }
}
