package net.qbar.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.qbar.QBar;
import net.qbar.common.tile.machine.TileBoiler;

public class GuiBoiler extends GuiContainer
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/boiler.png");

    private final TileBoiler              boiler;

    public GuiBoiler(final EntityPlayer player, final TileBoiler boiler)
    {
        super(boiler.createContainer(player));

        this.boiler = boiler;
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
                    Arrays.asList(TextFormatting.GOLD + "" + this.boiler.getHeat() / 10 + " / "
                            + this.boiler.getMaxHeat() / 10 + " °C"),
                    mouseX, mouseY, this.width, this.height, -1, this.mc.fontRendererObj);
        }
        else if (mouseX > x + 128 && mouseX < x + 146 && mouseY > y + 7 && mouseY < y + 80)
        {
            final List<String> lines = new ArrayList<>();
            if (this.boiler.getFluid() == null || this.boiler.getFluid().amount == 0)
                lines.add("Empty");
            else
            {
                lines.add(TextFormatting.GOLD + "" + this.boiler.getFluid().amount + " / "
                        + this.boiler.getFluidTank().getInternalFluidHandler().getTankProperties()[0].getCapacity()
                        + " mB");
            }
            GuiUtils.drawHoveringText(lines, mouseX, mouseY, this.width, this.height, -1, this.mc.fontRendererObj);
        }
        else if (mouseX > x + 151 && mouseX < x + 169 && mouseY > y + 7 && mouseY < y + 80)
        {
            final List<String> lines = new ArrayList<>();
            if (this.boiler.getSteamTank().toFluidStack().amount == 0)
                lines.add("Empty");
            else if (this.boiler.getSteamTank().getSteam() / this.boiler.getSteamTank().getCapacity() < 1)
                lines.add(TextFormatting.GOLD + "" + this.boiler.getSteamTank().getSteam() + " / "
                        + this.boiler.getSteamTank().getCapacity());
            else
            {
                lines.add((this.mc.world.getTotalWorldTime() / 10 % 2 == 0 ? TextFormatting.RED : TextFormatting.GOLD)
                        + "" + this.boiler.getSteamTank().getSteam() + " / "
                        + this.boiler.getSteamTank().getCapacity());
                lines.add((this.mc.world.getTotalWorldTime() / 10 % 2 == 0 ? TextFormatting.RED : TextFormatting.GOLD)
                        + "Overload!");
            }
            GuiUtils.drawHoveringText(lines, mouseX, mouseY, this.width, this.height, -1, this.mc.fontRendererObj);
        }
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        this.mc.renderEngine.bindTexture(GuiBoiler.BACKGROUND);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        final int burnProgress = this.getBurnLeftScaled(13);
        this.drawTexturedModalRect(x + 81, y + 38 - burnProgress, 176, 12 - burnProgress, 14, burnProgress + 1);

        final int heatProgress = this.getHeatScaled(71);
        this.drawTexturedModalRect(x + 10, y + 79 - heatProgress, 176, 85 - heatProgress, 12, heatProgress);

        this.drawFluid(this.boiler.getSteamTank().toFluidStack(), x + 151, y + 7, 18, 73,
                (int) (this.boiler.getSteamTank().getCapacity() * this.boiler.getSteamTank().getMaxPressure()));

        if (this.boiler.getFluid() != null)
            this.drawFluid(this.boiler.getFluid(), x + 128, y + 7, 18, 73,
                    this.boiler.getFluidTank().getInternalFluidHandler().getTankProperties()[0].getCapacity());

    }

    private void drawFluid(final FluidStack fluid, final int x, final int y, final int width, final int height,
            final int maxCapacity)
    {
        this.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        final ResourceLocation still = fluid.getFluid().getStill(fluid);
        final TextureAtlasSprite sprite = this.mc.getTextureMapBlocks().getAtlasSprite(still.toString());

        final int drawHeight = (int) (fluid.amount / (maxCapacity * 1F) * height);
        final int iconHeight = sprite.getIconHeight();
        int offsetHeight = drawHeight;

        int iteration = 0;
        while (offsetHeight != 0)
        {
            final int curHeight = offsetHeight < iconHeight ? offsetHeight : iconHeight;
            this.drawTexturedModalRect(x, y + height - offsetHeight, sprite, width, curHeight);
            offsetHeight -= curHeight;
            iteration++;
            if (iteration > 50)
                break;
        }
    }

    private int getHeatScaled(final int pixels)
    {
        final int i = this.boiler.getMaxHeat();

        if (i == 0)
            return -1;

        return Math.min(this.boiler.getHeat() * pixels / i, pixels);
    }

    private int getBurnLeftScaled(final int pixels)
    {
        final int i = this.boiler.getMaxBurnTime();

        if (i == 0)
            return -1;

        return this.boiler.getCurrentBurnTime() * pixels / i;
    }
}
