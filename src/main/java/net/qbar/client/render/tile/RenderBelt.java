package net.qbar.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.animation.FastTESR;
import net.qbar.common.tile.TileBelt;

public class RenderBelt extends FastTESR<TileBelt>
{
    protected static BlockRendererDispatcher blockRenderer;
    private TextureManager                   textureManager;

    @Override
    public void renderTileEntityFast(final TileBelt belt, final double x, final double y, final double z,
            final float partialTicks, final int destroyStage, final VertexBuffer renderer)
    {
        if (RenderBelt.blockRenderer == null)
            RenderBelt.blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        if (this.textureManager == null)
            this.textureManager = Minecraft.getMinecraft().getTextureManager();

        final BlockPos pos = belt.getPos();
        final IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(belt.getWorld(), pos);
        final IBlockState state = world.getBlockState(pos);

        renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

        final IBakedModel model = RenderBelt.blockRenderer.getBlockModelShapes().getModelForState(state);

        RenderBelt.blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, renderer, false);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        final int l = belt.getWorld().getCombinedLight(pos.offset(EnumFacing.UP),
                belt.getWorld().getSkylightSubtracted());
        final int j = l % 65536;
        final int k = l / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);

        GlStateManager.enableAlpha();
        GlStateManager.enableLighting();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.translate(1 - belt.getItemPositions()[1].y + 1 / 16.0, 1.438,
                belt.getItemPositions()[1].x + 7 / 64.0);
        this.handleRenderItem(belt.getItems()[1]);

        GlStateManager.translate(
                -(1 - belt.getItemPositions()[1].y + 1 / 16.0) + (1 - belt.getItemPositions()[0].y + 1 / 16.0), 0,
                -(belt.getItemPositions()[1].x + 7 / 64.0) + (belt.getItemPositions()[0].x + 7 / 64.0));
        this.handleRenderItem(belt.getItems()[0]);

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        RenderHelper.enableStandardItemLighting();
    }

    public void handleRenderItem(final ItemStack stack)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.translate(0.25, -0.25, 0);

        if (Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null).isGui3d())
        {
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            if (Block.getBlockFromItem(stack.getItem()) != null
                    && Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullCube())
                GlStateManager.translate(0, 0, -0.1);
        }
        else
        {

            GlStateManager.scale(0.48F, 0.5F, 0.48F);
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.rotate(90, 0, 0, 1);
            GlStateManager.translate(-.15, 0, .33);
        }

        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
    }
}
