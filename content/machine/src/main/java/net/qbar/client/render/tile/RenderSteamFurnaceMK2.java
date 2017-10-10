package net.qbar.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.qbar.client.render.RenderUtil;
import net.qbar.common.tile.machine.TileSteamFurnaceMK2;

public class RenderSteamFurnaceMK2 extends TileEntitySpecialRenderer<TileSteamFurnaceMK2>
{
    @Override
    public void render(TileSteamFurnaceMK2 tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + .42, y + 1.2, z + .8);

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
