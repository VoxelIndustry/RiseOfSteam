package net.qbar.client.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.qbar.client.render.model.FlattenedModelCache;
import net.qbar.client.render.tile.VisibilityModelState;
import net.qbar.common.multiblock.BlockMultiblockBase;

public class RenderUtil
{
    public static final void handleRenderItem(final ItemStack stack, final boolean render3d)
    {
        GlStateManager.pushMatrix();

        if (render3d)
        {
            if (Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null).isGui3d())
            {
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                if (Block.getBlockFromItem(stack.getItem()) != null
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

    public static final void renderMultiblock(final IBlockState state, final double x, final double y, final double z,
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

    public static final void renderMultiblock(final IBlockState state, final double x, final double y, final double z,
            final BlockRendererDispatcher blockRender, final List<BakedQuad> alphaQuads,
            final VisibilityModelState opaqueState)
    {
        final Minecraft minecraft = Minecraft.getMinecraft();

        minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);

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

    private static final void renderQuads(final List<BakedQuad> quads, final int alpha)
    {
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer buffer = tessellator.getBuffer();

        if (quads == null || quads.isEmpty())
            return;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        quads.forEach(quad -> LightUtil.renderQuadColor(buffer, quad, alpha | 0xffffff));
        tessellator.draw();
    }
}
