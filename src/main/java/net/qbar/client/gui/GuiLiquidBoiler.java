package net.qbar.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.qbar.QBar;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.common.tile.machine.TileLiquidBoiler;

import java.util.Arrays;

public class GuiLiquidBoiler extends GuiMachineBase<TileLiquidBoiler>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID,
            "textures/gui/liquidboiler.png");

    public GuiLiquidBoiler(final EntityPlayer player, final TileLiquidBoiler boiler)
    {
        super(player, boiler, BACKGROUND);

        this.addFluidTank(boiler.getWaterTank(), 128, 7, 18, 73);
        this.addFluidTank(boiler.getFuelTank(), 79, 7, 18, 73);
        this.addSteamTank(boiler.getSteamTank(), 151, 7, 18, 73);

        this.addAnimatedSprite(this::getHeatScaled,
                GuiProgress.builder().space(new GuiTexturedSpace(10, 79, 12, 78, 176, 84, 176 + 12, 85 + 79))
                        .direction(GuiProgress.StartDirection.TOP).revert(false).build());
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
                    mouseX, mouseY, this.width, this.height, -1, this.mc.fontRenderer);
        }
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
    }

    private int getHeatScaled(final int pixels)
    {
        final int i = this.getMachine().getMaxHeat();

        if (i == 0)
            return -1;

        return (int) Math.min(this.getMachine().getHeat() * pixels / i, pixels);
    }
}
