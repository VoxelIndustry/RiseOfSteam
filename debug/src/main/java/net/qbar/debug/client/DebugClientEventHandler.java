package net.qbar.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.qbar.debug.common.marker.MarkerData;
import net.qbar.debug.common.marker.MarkerDataHandler;
import org.lwjgl.opengl.GL11;

public class DebugClientEventHandler
{
    private EntityPlayerSP player;
    private double         playerX;
    private double         playerY;
    private double         playerZ;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent e)
    {
        if (player == null)
            player = Minecraft.getMinecraft().player;
        playerX = player.prevPosX + (player.posX - player.prevPosX) * e.getPartialTicks();
        playerY = player.prevPosY + (player.posY - player.prevPosY) * e.getPartialTicks();
        playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * e.getPartialTicks();

        MarkerDataHandler.getMarkersList().forEach((marker ->
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();

            RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(marker.getPos()).grow(0.005, 0.005, 0.005)
                            .offset(-playerX, -playerY, -playerZ),
                    0.8f, 0, 0, getAlpha(marker) + 0.2f);
            RenderGlobal.renderFilledBox(new AxisAlignedBB(marker.getPos()).grow(0.005, 0.005, 0.005)
                            .offset(-playerX, -playerY, -playerZ),
                    0.5f, 0, 0, getAlpha(marker));

            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
        }));

        MarkerDataHandler.getMarkersList().removeIf(marker ->
                marker.getStartTime() + marker.getDuration() <= System.currentTimeMillis());
    }

    private float getAlpha(MarkerData marker)
    {
        if (System.currentTimeMillis() - marker.getStartTime() < 125)
            return (System.currentTimeMillis() - marker.getStartTime()) / 125f * 0.6f;
        if (System.currentTimeMillis() - marker.getStartTime() < 250)
            return (1 - ((System.currentTimeMillis() - marker.getStartTime() - 125) / 125f)) * 0.4f + 0.2f;
        if (System.currentTimeMillis() - marker.getStartTime() > marker.getDuration() * 0.9f)
            return (marker.getDuration() - (System.currentTimeMillis() - marker.getStartTime()))
                    / (marker.getDuration() * 0.9f) * 0.2f;
        return 0.2f;
    }
}
