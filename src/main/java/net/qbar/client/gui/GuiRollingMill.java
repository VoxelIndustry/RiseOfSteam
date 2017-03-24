package net.qbar.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.common.tile.machine.TileRollingMill;

public class GuiRollingMill extends GuiContainer
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/rollingmill.png");

    private final TileRollingMill        rollingmill;

    public GuiRollingMill(final EntityPlayer player, final TileRollingMill rollingmill)
    {
        super(rollingmill.createContainer(player));

        this.rollingmill = rollingmill;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        this.mc.renderEngine.bindTexture(GuiRollingMill.BACKGROUND);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        final int j = this.rollingmill.getProgressScaled(24);
        if (j > 0)
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, j + 1, 16);
    }
}
