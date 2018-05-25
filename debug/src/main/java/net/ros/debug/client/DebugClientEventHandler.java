package net.ros.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.ros.debug.common.counter.CounterData;
import net.ros.debug.common.counter.CounterDataHandler;
import net.ros.debug.common.marker.MarkerData;
import net.ros.debug.common.marker.MarkerDataHandler;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;

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

        CounterDataHandler.getCountersMap().forEach((pos, counters) ->
        {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.translate(pos.getX() - playerX + 0.5, pos.getY() - playerY + 1.1 +
                            (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * (0.625f / 48) * counters.size()),
                    pos.getZ() - playerZ + 0.5);

            EnumFacing facing = player.getHorizontalFacing();

            if (facing.getAxis() == EnumFacing.Axis.X)
                GlStateManager.rotate(player.getHorizontalFacing().getHorizontalAngle() + 180, 0, 1, 0);
            else
                GlStateManager.rotate(player.getHorizontalFacing().getHorizontalAngle(), 0, 1, 0);
            GlStateManager.rotate(180, 0, 0, 1);
            GlStateManager.scale(0.625f / 48, 0.625f / 48, 0.625f / 48);
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1, 1, 1, 1);

            int i = 0;
            for (CounterData counter : counters)
            {
                String text = TextFormatting.AQUA + counter.getKey() + ": " + TextFormatting.GOLD + counter.getCount();

                Minecraft.getMinecraft().fontRenderer.drawString(text,
                        -Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2,
                        i * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT, 0);
                i++;
            }

            Iterator<CounterData> iterator = counters.iterator();

            while(iterator.hasNext())
            {
                CounterData counter = iterator.next();

                if(counter.getStartTime() + counter.getDuration() <= System.currentTimeMillis())
                    iterator.remove();
            }

            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        });

        CounterDataHandler.getCountersMap().entrySet().removeIf(entry -> entry.getValue().isEmpty());
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
