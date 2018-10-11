package net.ros.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.ros.common.block.BlockPipeCover;
import net.ros.common.block.PipeCoverType;
import net.ros.common.grid.node.IPipeValve;
import net.ros.common.steam.SteamUtil;
import net.ros.common.tile.TileSteamGauge;
import net.voxelindustry.brokkgui.paint.Color;

public class LogisticClientEventHandler
{
    private Color red   = Color.fromHex("#DF310C");
    private Color green = Color.fromHex("#13AD52");
    private Color white = Color.fromHex("#E2F3E9");

    @SubscribeEvent
    public void onDrawblockHightlight(final DrawBlockHighlightEvent e)
    {
        if (!e.getTarget().typeOfHit.equals(RayTraceResult.Type.BLOCK))
            return;

        Block target = e.getPlayer().world.getBlockState(e.getTarget().getBlockPos()).getBlock();

        if (!(target instanceof BlockPipeCover))
            return;
        if (((BlockPipeCover) target).getCoverType() == PipeCoverType.VALVE)
            renderValveOverlay(e.getPlayer(), e.getTarget().getBlockPos(), e.getPartialTicks());
        else if (((BlockPipeCover) target).getCoverType() == PipeCoverType.STEAM_GAUGE)
            renderGaugeOverlay(e.getPlayer(), e.getTarget().getBlockPos(), e.getPartialTicks());
    }

    private void renderValveOverlay(EntityPlayer player, BlockPos pos, float partialTicks)
    {
        final double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(pos.getX() - x + 0.5, pos.getY() - y + 0.5, pos.getZ() - z + 0.5);

        EnumFacing facing = player.getHorizontalFacing();

        if (facing.getAxis() == EnumFacing.Axis.X)
            GlStateManager.rotate(player.getHorizontalFacing().getHorizontalAngle() + 180, 0, 1, 0);
        else
            GlStateManager.rotate(player.getHorizontalFacing().getHorizontalAngle(), 0, 1, 0);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.translate(0, 0, -0.5);
        GlStateManager.scale(0.625f / 36, 0.625f / 36, 0.625f / 36);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);

        IPipeValve valve = (IPipeValve) player.getEntityWorld().getTileEntity(pos);

        String text = valve.isOpen() ? I18n.format("valve.status.open") : I18n.format("valve.status.close");

        Minecraft.getMinecraft().fontRenderer.drawString(text,
                -Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2, 0, valve.isOpen() ?
                        green.toRGBInt() : red.toRGBInt());

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void renderGaugeOverlay(EntityPlayer player, BlockPos pos, float partialTicks)
    {
        final double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(pos.getX() - x + 0.5, pos.getY() - y + 0.5, pos.getZ() - z + 0.5);

        EnumFacing facing = player.getHorizontalFacing();

        if (facing.getAxis() == EnumFacing.Axis.X)
            GlStateManager.rotate(player.getHorizontalFacing().getHorizontalAngle() + 180, 0, 1, 0);
        else
            GlStateManager.rotate(player.getHorizontalFacing().getHorizontalAngle(), 0, 1, 0);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.translate(0, 0, -0.5);
        GlStateManager.scale(0.625f / 36, 0.625f / 36, 0.625f / 36);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);

        TileSteamGauge gauge = (TileSteamGauge) player.getEntityWorld().getTileEntity(pos);

        String text = SteamUtil.pressureFormat.format(gauge.getCurrentPressure())
                + " / " + SteamUtil.pressureFormat.format(gauge.getMaxPressure());
        float ratio = gauge.getCurrentPressure() / (gauge.getMaxPressure() * 1.5f);
        if (ratio < 0.5f)
            ratio = 0;

        Minecraft.getMinecraft().fontRenderer.drawString(text,
                -Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2, 0, getColor(ratio));

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private int getColor(float ratio)
    {
        Color from = white;
        Color to = red;
        float interpR = ((to.getRed() - from.getRed()) * ratio + from.getRed());
        float interpG = ((to.getGreen() - from.getGreen()) * ratio + from.getGreen());
        float interpB = ((to.getBlue() - from.getBlue()) * ratio + from.getBlue());

        return new Color(interpR, interpG, interpB).toRGBInt();
    }
}
