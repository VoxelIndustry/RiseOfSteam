package net.qbar.client.render;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.qbar.common.multiblock.TileMultiblockGag;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.multiblock.blueprint.BlueprintState;
import net.qbar.common.tile.TileStructure;

public class RenderStructureOverlay
{
    public static final void renderStructureOverlay(final EntityPlayer player, final BlockPos pos,
            final float partialTicks)
    {
        final TileEntity tile = player.getEntityWorld().getTileEntity(pos);

        if (tile != null)
        {
            TileStructure structure = null;
            if (tile instanceof TileMultiblockGag && ((TileMultiblockGag) tile).getCore() instanceof TileStructure)
                structure = (TileStructure) ((TileMultiblockGag) tile).getCore();
            else if (tile instanceof TileStructure)
                structure = (TileStructure) tile;

            if (structure != null && structure.getBlueprint() != null)
            {
                final double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
                final double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
                final double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

                GlStateManager.pushMatrix();
                GlStateManager.translate(pos.getX() - x + 0.5, pos.getY() - y, pos.getZ() - z + .5);
                GlStateManager.rotate(180, 1, 0, 0);
                GlStateManager.rotate(player.getHorizontalFacing().getHorizontalAngle() - 180, 0, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, -1, -.51);
                GlStateManager.scale(0.625f / 48, 0.625f / 48, 0.625f / 48);
                GlStateManager.disableLighting();
                GlStateManager.disableBlend();

                final String name = I18n.translateToLocal(
                        Block.getBlockFromName("qbar:" + structure.getBlueprint().getName()).getUnlocalizedName()
                                + ".name");
                Minecraft.getMinecraft().fontRendererObj.drawString(name,
                        -Minecraft.getMinecraft().fontRendererObj.getStringWidth(name) / 2, 0, 16777215);

                GlStateManager.popMatrix();
                GlStateManager.translate(-0.3, -0.8, -.51);

                GlStateManager.scale(0.25, 0.25, 0.25);

                final int currentStep = structure.getBlueprintState().getCurrentStep();
                final Blueprint blueprint = structure.getBlueprint();
                final BlueprintState blueprintState = structure.getBlueprintState();

                int step = 0;
                for (final List<ItemStack> stackList : structure.getBlueprint().getSteps())
                {
                    RenderStructureOverlay.renderStep(step, stackList, blueprintState);
                    step++;
                }
                GlStateManager.popMatrix();
                GlStateManager.enableLighting();
            }
        }
    }

    private static void renderStep(final int step, final List<ItemStack> stackList, final BlueprintState state)
    {
        if (step != 0)
            GlStateManager.translate(0, 1, 0);
        GlStateManager.pushMatrix();
        for (int stack = 0; stack < stackList.size(); stack++)
        {
            if (stack != 0)
                GlStateManager.translate(1, 0, 0);
            RenderUtil.handleRenderItem(stackList.get(stack), false);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.2, 0.15, -.02);
            GlStateManager.scale(0.625f / 48, 0.625f / 48, 0.625f / 48);

            String count = "";
            if (step == state.getCurrentStep())
                count = state.getCurrentStacks().get(stack).getCount() - 1 + "/" + stackList.get(stack).getCount();
            else if (step < state.getCurrentStep())
                count = stackList.get(stack).getCount() + "/" + stackList.get(stack).getCount();
            else
                count = "0/" + stackList.get(stack).getCount();

            GlStateManager.disableLighting();
            Minecraft.getMinecraft().fontRendererObj.drawString(count,
                    -Minecraft.getMinecraft().fontRendererObj.getStringWidth(count) / 2, 0, 16777215);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
