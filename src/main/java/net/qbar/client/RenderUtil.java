package net.qbar.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

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
}
