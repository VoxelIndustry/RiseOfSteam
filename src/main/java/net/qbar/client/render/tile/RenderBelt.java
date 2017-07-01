package net.qbar.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.FastTESR;
import net.qbar.client.render.RenderUtil;
import net.qbar.common.block.BlockBelt.EBeltSlope;
import net.qbar.common.grid.ItemBelt;
import net.qbar.common.tile.machine.TileBelt;

public class RenderBelt extends FastTESR<TileBelt>
{
    @Override
    public void renderTileEntityFast(final TileBelt belt, final double x, final double y, final double z,
            final float partialTicks, final int destroyStage, final float partial, final BufferBuilder renderer)
    {
        final BlockPos pos = belt.getBlockPos();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        final int l = belt.getBlockWorld().getCombinedLight(pos.offset(EnumFacing.UP),
                belt.getBlockWorld().getSkylightSubtracted());
        final int j = l % 65536;
        final int k = l / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);

        GlStateManager.enableAlpha();
        GlStateManager.enableLighting();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        switch (belt.getFacing())
        {
            case NORTH:
                GlStateManager.rotate(90, 0, 1, 0);
                GlStateManager.translate(-1, 0, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(-90, 0, 1, 0);
                GlStateManager.translate(0, 0, -1);
                break;
            case WEST:
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(-1, 0, -1);
                break;
            default:
                break;
        }

        GlStateManager.translate(0, 1.438, 0);
        if (belt.isSlope())
        {
            if (belt.getSlopeState().equals(EBeltSlope.DOWN))
            {
                GL11.glRotated(-45, 0, 0, 1);
                GlStateManager.translate(5 / 16F, 2 / 16F, 0);
            }
            else
            {
                GL11.glRotated(45, 0, 0, 1);
                GlStateManager.translate(-11 / 16F, -.58f, 0);
            }
        }

        ItemBelt previous = null;
        for (final ItemBelt item : belt.getItems())
        {
            if (previous == null)
                GlStateManager.translate(1 + item.getPos().y - 9 / 16.0, 0, item.getPos().x + 7 / 64.0);
            else
                GlStateManager.translate(-(1 + previous.getPos().y - 9 / 16.0) + (1 + item.getPos().y - 9 / 16.0), 0,
                        -(previous.getPos().x + 7 / 64.0) + (item.getPos().x + 7 / 64.0));
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180, 0, 1, 0);
            GlStateManager.translate(0.25, -0.25, 0);
            RenderUtil.handleRenderItem(item.getStack(), true);
            GlStateManager.popMatrix();
            previous = item;
        }

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        RenderHelper.enableStandardItemLighting();
    }
}
