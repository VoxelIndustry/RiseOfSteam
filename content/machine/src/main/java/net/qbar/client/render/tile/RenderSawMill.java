package net.qbar.client.render.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.model.animation.FastTESR;
import net.qbar.client.render.RenderUtil;
import net.qbar.common.tile.machine.TileSawMill;

public class RenderSawMill extends FastTESR<TileSawMill>
{
    @Override
    public void renderTileEntityFast(final TileSawMill tile, final double x, final double y, final double z,
                                     final float partialTicks, final int destroyStage, final float partial, final
                                     BufferBuilder VertexBuffer)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + .42, y + 1.2, z + 1.8);

        switch (tile.getFacing())
        {
            case NORTH:
                GlStateManager.rotate(90, 0, 1, 0);
                GlStateManager.translate(2.6, 0, 0.16);
                break;
            case SOUTH:
                GlStateManager.rotate(-90, 0, 1, 0);
                break;
            case WEST:
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(1.22, 0, 1.38);
                break;
            default:
                GlStateManager.translate(1.38, 0, -1.22);
                break;
        }

        if (!tile.getStackInSlot(0).isEmpty())
            RenderUtil.handleRenderItem(tile.getStackInSlot(0), true);
        if (!tile.getStackInSlot(2).isEmpty())
        {
            GlStateManager.translate(-3 * (tile.getCurrentProgress() / tile.getMaxProgress()), 0, 0);

            if (tile.getCurrentProgress() / tile.getMaxProgress() > 0.5)
                RenderUtil.handleRenderItem(tile.getCachedStack(), true);
            else
                RenderUtil.handleRenderItem(tile.getStackInSlot(2), true);
        }
        GlStateManager.popMatrix();
    }
}
