package net.qbar.client.gui;

import java.util.Arrays;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.qbar.QBar;
import net.qbar.common.tile.machine.TileSolarBoiler;

public class GuiSolarBoiler extends GuiMachineBase<TileSolarBoiler>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID,
            "textures/gui/solarboiler.png");

    public GuiSolarBoiler(final EntityPlayer player, final TileSolarBoiler boiler)
    {
        super(player, boiler);

        this.addFluidTank(boiler.getFluidTank(), 128, 7, 18, 73);
        this.addSteamTank(boiler.getSteamTank(), 151, 7, 18, 73);
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
                    Arrays.asList(TextFormatting.GOLD + "" + this.getMachine().getHeat() / 10 + " / "
                            + this.getMachine().getMaxHeat() / 10 + " °C"),
                    mouseX, mouseY, this.width, this.height, -1, this.mc.fontRendererObj);
        }
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        this.mc.renderEngine.bindTexture(BACKGROUND);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        final int heatProgress = this.getHeatScaled(71);
        this.drawTexturedModalRect(x + 10, y + 79 - heatProgress, 176, 85 - heatProgress, 12, heatProgress);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    private int getHeatScaled(final int pixels)
    {
        final int i = this.getMachine().getMaxHeat();

        if (i == 0)
            return -1;

        return (int) Math.min(this.getMachine().getHeat() * pixels / i, pixels);
    }
}
