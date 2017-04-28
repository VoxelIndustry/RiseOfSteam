package net.qbar.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.qbar.QBar;
import net.qbar.common.tile.machine.TileSteamFurnaceMK2;

import java.util.Arrays;

public class GuiSteamFurnaceMK2 extends GuiMachineBase<TileSteamFurnaceMK2>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/steamfurnace.png");

    public GuiSteamFurnaceMK2(final EntityPlayer player, final TileSteamFurnaceMK2 steamfurnace)
    {
        super(player, steamfurnace);

        this.addSteamTank(steamfurnace.getSteamTank(), 151, 7, 18, 73);
    }

    @Override
    public void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        GlStateManager.translate(-this.guiLeft, -this.guiTop, 0.0F);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        if (mouseX > x + 10 && mouseX < x + 22 && mouseY > y + 8 && mouseY < y + 79)
        {
            GuiUtils.drawHoveringText(
                    Arrays.asList(TextFormatting.GOLD + "" + this.getMachine().getCurrentHeat() + " / "
                            + this.getMachine().getMaxHeat() + " °C"),
                    mouseX, mouseY, this.width, this.height, -1, this.mc.fontRendererObj);
        }
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        this.mc.renderEngine.bindTexture(GuiSteamFurnace.BACKGROUND);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        final int j = this.getMachine().getProgressScaled(24);
        if (j > 0)
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, j + 1, 16);

        final int heatProgress = this.getHeatScaled(71);
        this.drawTexturedModalRect(x + 10, y + 79 - heatProgress, 176, 102 - heatProgress, 12, heatProgress);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    private int getHeatScaled(final int pixels)
    {
        final int i = this.getMachine().getMaxHeat();

        if (i == 0)
            return -1;

        return Math.min(this.getMachine().getCurrentHeat() * pixels / i, pixels);
    }
}
