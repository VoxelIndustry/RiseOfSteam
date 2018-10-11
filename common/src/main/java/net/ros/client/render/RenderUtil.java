package net.ros.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.ros.client.render.model.FlattenedModelCache;
import net.ros.client.render.tile.VisibilityModelState;
import net.ros.common.multiblock.BlockMultiblockBase;
import net.voxelindustry.brokkgui.paint.Color;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RenderUtil
{
    public static void handleRenderItem(final ItemStack stack, final boolean render3d)
    {
        GlStateManager.pushMatrix();

        if (render3d)
        {
            if (Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null).isGui3d())
            {
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR
                        && Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullCube())
                    GlStateManager.translate(0, 0, -0.1);
            }
            else
            {
                GlStateManager.scale(0.46F, 0.5F, 0.46F);
                GlStateManager.rotate(90, 1, 0, 0);
                GlStateManager.rotate(90, 0, 0, 1);
                GlStateManager.translate(0, 0.025, .33);
            }

            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        }
        else
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                    .setBlurMipmap(false, false);

            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderHelper.enableStandardItemLighting();

            GlStateManager.scale(0.5F, 0.5F, 0.02F);

            Minecraft.getMinecraft().getRenderItem().renderItem(stack,
                    FlattenedModelCache.getInstance().getFlattenedModel(
                            Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null)));
            GlStateManager.disableRescaleNormal();
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                    .restoreLastBlurMipmap();
        }
        GlStateManager.popMatrix();
    }

    public static void renderMultiblock(final IBlockState state, final double x, final double y, final double z,
                                        final BlockRendererDispatcher blockRender)
    {
        final Minecraft minecraft = Minecraft.getMinecraft();

        minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1f, 1f, 1f, 1f);
        final int alpha = (int) (0.5 * 0xFF) << 24;
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        RenderUtil.renderQuads(blockRender.getModelForState(state).getQuads(state, null, 0), alpha);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderMultiblock(final IBlockState state, final double x, final double y, final double z,
                                        final BlockRendererDispatcher blockRender, final List<BakedQuad> alphaQuads,
                                        final VisibilityModelState opaqueState)
    {
        final Minecraft minecraft = Minecraft.getMinecraft();

        minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT_MIPPED);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableTexture2D();

        final IBakedModel model = blockRender.getModelForState(state);

        blockRender.getBlockModelRenderer().renderModelBrightnessColor(
                ((BlockMultiblockBase) state.getBlock()).getGhostState(state, opaqueState), model, 1, 1, 1, 1);

        final int alpha = (int) (0.6 * 0xFF) << 24;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RenderUtil.renderQuads(alphaQuads, alpha);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderQuads(final List<BakedQuad> quads, final int alpha)
    {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        if (quads == null || quads.isEmpty())
            return;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        quads.forEach(quad -> LightUtil.renderQuadColor(buffer, quad, alpha | 0xffffff));
        tessellator.draw();
    }

    public static void renderRect(final double left, final double top, final double right, final double bottom,
                                  final float r, final float g, final float b, final float a)
    {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.color(r, g, b, a);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(left, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, top, 0.0D).endVertex();
        vertexbuffer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void renderTexOnSide(double x, double y, double z, double width, double height, double offset,
                                       EnumFacing facing, ResourceLocation tex)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(tex);

        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);

        GlStateManager.rotate(180 - facing.getHorizontalAngle(), 0, 1, 0);

        if (facing.getAxis().isVertical())
            GlStateManager.rotate(facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 90 : -90, 1, 0, 0);

        Tessellator t = Tessellator.getInstance();
        BufferBuilder buffer = t.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(width / 2, -height / 2, offset).tex(1, 0).endVertex();
        buffer.pos(-width / 2, -height / 2, offset).tex(0, 0).endVertex();
        buffer.pos(-width / 2, height / 2, offset).tex(0, 1).endVertex();
        buffer.pos(width / 2, height / 2, offset).tex(1, 1).endVertex();
        t.draw();

        GlStateManager.popMatrix();
    }

    public static void renderTextOnSide(double x, double y, double z, double scale, double offset,
                                        EnumFacing facing, String text, Color color)
    {
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);

        GlStateManager.rotate(180 - facing.getHorizontalAngle(), 0, 1, 0);
        GlStateManager.rotate(180, 0, 0, 1);
        if (facing.getAxis().isVertical())
            GlStateManager.rotate(facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 90 : -90, 1, 0, 0);

        GlStateManager.translate(0, 0, offset);
        GlStateManager.scale(scale, scale, scale);
        Minecraft.getMinecraft().fontRenderer.drawString(text,
                -Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2, 0, color.toRGBInt());

        GlStateManager.popMatrix();
    }
}
