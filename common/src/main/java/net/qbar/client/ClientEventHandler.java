package net.qbar.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.qbar.common.block.IComplexSelectBox;

public class ClientEventHandler
{
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event)
    {
        World w = event.getPlayer().world;

        if (event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK ||
                !(w.getBlockState(event.getTarget().getBlockPos()).getBlock() instanceof IComplexSelectBox))
            return;

        event.setCanceled(true);

        AxisAlignedBB selectBox = ((IComplexSelectBox) w.getBlockState(event.getTarget().getBlockPos()).getBlock())
                .getSelectedBox(event.getPlayer(), event.getTarget().getBlockPos(), event.getPartialTicks());

        EntityPlayer player = event.getPlayer();
        double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks();
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor
                .ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        if (selectBox != null)
            RenderGlobal.drawSelectionBoundingBox(selectBox.grow(0.0020000000949949026D)
                    .offset(-playerX, -playerY, -playerZ), 0, 0, 0, 0.4f);

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
