package net.qbar.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.qbar.QBar;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.tile.machine.TileSteamFurnaceMK2;

import java.util.Arrays;

public class GuiSteamFurnaceMK2 extends GuiMachineBase<TileSteamFurnaceMK2>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/steamfurnace.png");

    public GuiSteamFurnaceMK2(final EntityPlayer player, final TileSteamFurnaceMK2 steamfurnace)
    {
        super(player, steamfurnace, BACKGROUND);

        this.addSteamTank(steamfurnace.getSteamTank(), 151, 7, 18, 73);

        this.addAnimatedSprite(this::getHeatScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(10).y(79).width(12).height(78).u(176).v(102)
                        .s(176 + 12).t(102 + 79).build()).direction(GuiProgress.StartDirection.TOP).revert(false)
                        .build());
        this.addAnimatedSprite(this.getMachine()::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(79).y(34).width(25).height(16).u(176).v(14).s
                        (176 + 25).t(14 + 16).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());
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
                    mouseX, mouseY, this.width, this.height, -1, this.mc.fontRenderer);
        }
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
    }

    private int getHeatScaled(final int pixels)
    {
        final int i = this.getMachine().getMaxHeat();

        if (i == 0)
            return -1;

        return Math.min(this.getMachine().getCurrentHeat() * pixels / i, pixels);
    }
}
