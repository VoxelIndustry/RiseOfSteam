package net.qbar.client;

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
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class RenderUtil
{
    public static final void handleRenderItem(final ItemStack stack)
    {
        GlStateManager.pushMatrix();

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
        GlStateManager.popMatrix();
    }

    public static final void renderMultiblock(final IBlockState state, final BlockPos pos, final float partialTicks,
            final EntityPlayer player, final BlockRendererDispatcher blockRender)
    {
        final double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        final Minecraft minecraft = Minecraft.getMinecraft();
        final World world = player.world;

        minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);

        GlStateManager.pushMatrix();
        GlStateManager.translate(pos.getX() - dx, pos.getY() - dy, pos.getZ() - dz);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1f, 1f, 1f, 1f);
        final int alpha = (int) (0.5 * 0xFF) << 24;
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        RenderUtil.renderQuads(blockRender.getModelForState(state)
                .getQuads(state.getBlock().getExtendedState(state, world, pos), null, 0), alpha);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static final void renderQuads(final List<BakedQuad> quads, final int alpha)
    {
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer buffer = tessellator.getBuffer();

        if (quads == null || quads.isEmpty())
            return;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        quads.forEach(quad -> LightUtil.renderQuadColor(buffer, quad, alpha | 0xffffff));
        tessellator.draw();
    }
}
