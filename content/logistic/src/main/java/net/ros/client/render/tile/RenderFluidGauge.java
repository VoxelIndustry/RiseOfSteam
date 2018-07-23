package net.ros.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.tile.TileFluidGauge;
import org.lwjgl.opengl.GL11;
import org.yggard.brokkgui.paint.Color;

public class RenderFluidGauge extends TileEntitySpecialRenderer<TileFluidGauge>
{
    @Override
    public final void render(TileFluidGauge gauge, double x, double y, double z, float partialTicks, int destroyStage,
                             float partial)
    {
        GlStateManager.pushMatrix();


        int capacity = gauge.getBufferTank().getCapacity();
        FluidStack fluid = gauge.getBufferTank().getFluid();
        if (fluid != null)
        {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();

            GlStateManager.translate(x + .5, y, z + .5);

            EnumFacing facing = gauge.getFacing();

            if (facing == EnumFacing.NORTH)
                GlStateManager.rotate(180, 0, 1, 0);
            else if (facing == EnumFacing.SOUTH)
                GlStateManager.rotate(0, 0, 1, 0);
            else if (facing == EnumFacing.EAST)
                GlStateManager.rotate(90, 0, 1, 0);
            else if (facing == EnumFacing.WEST)
                GlStateManager.rotate(-90, 0, 1, 0);

            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite still =
                    Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString());

            Color color = Color.fromRGBAInt(fluid.getFluid().getColor(fluid));

            double startX = -1.25 / 16D;
            double endX = startX + 2.5 / 16D;

            double startY = 3.75 / 16D;
            double endY = startY + (8.5 / 16D * ((float) fluid.amount / (float) capacity));
            double endZ = 8.25 / 16D + 0.001;

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.depthFunc(GL11.GL_LEQUAL);

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(endX, startY, endZ)
                    .tex(still.getInterpolatedU(12), still.getInterpolatedV(15))
                    .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            buffer.pos(endX, endY, endZ)
                    .tex(still.getInterpolatedU(12), still.getInterpolatedV(1))
                    .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            buffer.pos(startX, endY, endZ)
                    .tex(still.getInterpolatedU(4), still.getInterpolatedV(1))
                    .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            buffer.pos(startX, startY, endZ)
                    .tex(still.getInterpolatedU(4), still.getInterpolatedV(15))
                    .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            tess.draw();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();

            buffer.setTranslation(0, 0, 0);
        }
        GlStateManager.popMatrix();
    }

}
