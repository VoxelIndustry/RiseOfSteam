package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.tile.machine.TileBlueprintPrinter;
import net.qbar.common.tile.machine.TileCraftCardLibrary;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

public class GuiCraftCardLibrary extends BrokkGuiContainer<BuiltContainer>
{
    private static final int xSize = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(QBar.MODID + ":textures/gui/craftcardlibrary.png", 0, 0,
            xSize / 256.0f, ySize / 256.0f);

    private final TileCraftCardLibrary craftCardLibrary;

    public GuiCraftCardLibrary(final EntityPlayer player, final TileCraftCardLibrary craftCardLibrary)
    {
        super(craftCardLibrary.createContainer(player));
        this.setWidth(xSize);
        this.setHeight(ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.craftCardLibrary = craftCardLibrary;
    }
}
