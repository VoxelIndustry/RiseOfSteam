package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.tile.machine.TileBlueprintPrinter;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiBlueprintPrinter extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(QBar.MODID + ":textures/gui/blueprintprinter.png", 0, 0,
            xSize / 256.0f, ySize / 256.0f);

    private final TileBlueprintPrinter blueprintPrinter;

    private final GuiAbsolutePane blueprintPane;

    public GuiBlueprintPrinter(final EntityPlayer player, final TileBlueprintPrinter blueprintPrinter)
    {
        super(blueprintPrinter.createContainer(player));
        this.setWidth(xSize);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.blueprintPrinter = blueprintPrinter;

        final GuiAbsolutePane mainPanel = new GuiAbsolutePane();
        this.setMainPanel(mainPanel);

        this.blueprintPane = new GuiAbsolutePane();
        this.blueprintPane.setWidth(176);
        this.blueprintPane.setHeight(100);
        mainPanel.addChild(this.blueprintPane, 0, 66);

        this.initBlueprints();
    }

    private void initBlueprints()
    {

    }
}
