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
import net.qbar.common.tile.machine.TileTank;

public class GuiFluidTank extends GuiContainer
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/fluidtank.png");

    private final TileTank               fluidtank;

    public GuiFluidTank(final EntityPlayer player, final TileTank fluidtank)
    {
        super(fluidtank.createContainer(player));

        this.fluidtank = fluidtank;
    }

    @Override
    public void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        GlStateManager.translate(-this.guiLeft, -this.guiTop, 0.0F);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        if (mouseX > x + 78 && mouseX < x + 94 && mouseY > y + 7 && mouseY < y + 80)
        {
            final List<String> lines = new ArrayList<>();
            if (this.fluidtank.getFluid() == null || this.fluidtank.getFluid().amount == 0)
                lines.add("Empty");
            else
            {
                lines.add(TextFormatting.GOLD + this.fluidtank.getFluid().getLocalizedName());
                lines.add(TextFormatting.GOLD + "" + this.fluidtank.getFluid().amount + " / "
                        + this.fluidtank.getTank().getCapacity() + " mB");
            }
            GuiUtils.drawHoveringText(lines, mouseX, mouseY, this.width, this.height, -1, this.mc.fontRendererObj);
        }
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        this.mc.renderEngine.bindTexture(GuiFluidTank.BACKGROUND);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        if (this.fluidtank.getFluid() != null)
            this.drawFluid(this.fluidtank.getFluid(), x + 78, y + 7, 18, 73, this.fluidtank.getTank().getCapacity());
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
