package net.qbar.client.gui;

import java.util.ArrayList;
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
import net.qbar.common.tile.machine.TileSteamFurnace;

public class GuiSteamFurnace extends GuiContainer
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/steamfurnace.png");

    private final TileSteamFurnace       steamfurnace;

    public GuiSteamFurnace(final EntityPlayer player, final TileSteamFurnace steamfurnace)
    {
        super(steamfurnace.createContainer(player));

        this.steamfurnace = steamfurnace;
    }

    @Override
    public void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        GlStateManager.translate(-this.guiLeft, -this.guiTop, 0.0F);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        if (mouseX > x + 151 && mouseX < x + 169 && mouseY > y + 7 && mouseY < y + 80)
        {
            final List<String> lines = new ArrayList<>();
            if (this.steamfurnace.getSteamTank().toFluidStack().amount == 0)
                lines.add("Empty");
            else if (this.steamfurnace.getSteamTank().getSteam() / this.steamfurnace.getSteamTank().getCapacity() < 1)
                lines.add(TextFormatting.GOLD + "" + this.steamfurnace.getSteamTank().getSteam() + " / "
                        + this.steamfurnace.getSteamTank().getCapacity());
            else
            {
                lines.add((this.mc.world.getTotalWorldTime() / 10 % 2 == 0 ? TextFormatting.RED : TextFormatting.GOLD)
                        + "" + this.steamfurnace.getSteamTank().getSteam() + " / "
                        + this.steamfurnace.getSteamTank().getCapacity());
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
        this.mc.renderEngine.bindTexture(GuiSteamFurnace.BACKGROUND);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        final int j = this.steamfurnace.getProgressScaled(24);
        if (j > 0)
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, j + 1, 16);

        this.drawFluid(this.steamfurnace.getSteamTank().toFluidStack(), x + 151, y + 7, 18, 73,
                (int) (this.steamfurnace.getSteamTank().getCapacity()
                        * this.steamfurnace.getSteamTank().getMaxPressure()));
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
}
