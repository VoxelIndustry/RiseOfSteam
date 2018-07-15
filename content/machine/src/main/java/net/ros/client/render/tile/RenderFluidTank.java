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
import net.minecraftforge.fluids.IFluidTank;
import net.ros.common.machine.module.impl.FluidStorageModule;
import net.ros.common.tile.machine.TileTank;
import org.lwjgl.opengl.GL11;
import org.yggard.brokkgui.paint.Color;

public class RenderFluidTank extends TileEntitySpecialRenderer<TileTank>
{
    @Override
    public final void render(TileTank tank, double x, double y, double z, float partialTicks, int destroyStage,
                             float partial)
    {
        GlStateManager.pushMatrix();

        IFluidTank fluidTank = (IFluidTank) tank.getModule(FluidStorageModule.class).getFluidHandler("fluid");

        int capacity = fluidTank.getCapacity();
        FluidStack fluid = fluidTank.getFluid();
        if (fluid != null)
        {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();

            GlStateManager.translate(x + .5, y, z + .5);

            EnumFacing facing = tank.getFacing();

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

            double tankXOffset = tank.getTier() == 1 ? 6 / 16D : 0;
            double tankYOffset = tank.getTier() == 0 ? 0 : (tank.getTier() == 1 ? -6 / 16D : 3.5 / 16D);
            double tankZOffset = tank.getTier() == 0 ? 0 : (tank.getTier() == 1 ? 15.5 / 16D : 13.5 / 16D);

            double startX = tankXOffset - 2 / 16D;
            double endX = startX + 4 / 16D;

            double startY = tankYOffset + 19 / 16D;
            double endY = startY + (14 / 16D * ((float) fluid.amount / (float) capacity));
            double endZ = tankZOffset + 7 / 16D + 0.001;

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

    public void drawTexturedModalRect(double x, double y, double z, TextureAtlasSprite textureSprite, double w,
                                      double h)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + h, z).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(x + w, y + h, z).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        bufferbuilder.pos(x + w, y, z).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        bufferbuilder.pos(x, y, z).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }
}
