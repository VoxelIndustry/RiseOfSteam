package net.ros.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.ros.common.ROSConstants;
import net.ros.common.multiblock.TileMultiblockGag;
import net.ros.common.multiblock.blueprint.BlueprintState;
import net.ros.common.tile.TileStructure;

import java.util.List;

public class RenderStructureOverlay
{
    public static void renderStructureOverlay(final EntityPlayer player, final BlockPos pos,
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
                        Block.getBlockFromName(ROSConstants.MODID + ":" +
                                structure.getBlueprint().getDescriptor().getName()).getTranslationKey() + ".name");
                Minecraft.getMinecraft().fontRenderer.drawString(name,
                        -Minecraft.getMinecraft().fontRenderer.getStringWidth(name) / 2, 0, 16777215);

                GlStateManager.popMatrix();
                GlStateManager.translate(-0.3, -0.8, -.56);

                GlStateManager.scale(0.25, 0.25, 0.25);

                final BlueprintState blueprintState = structure.getBlueprintState();

                if (player.isSneaking())
                    RenderStructureOverlay.renderStepDetail(blueprintState);
                else
                {
                    int step = 0;
                    for (final List<ItemStack> stackList : structure.getBlueprint().getSteps())
                    {
                        RenderStructureOverlay.renderStep(step, stackList, blueprintState);
                        step++;
                    }
                }
                GlStateManager.popMatrix();
                GlStateManager.enableLighting();
            }
        }
    }

    private static void renderStepDetail(BlueprintState state)
    {
        GlStateManager.pushMatrix();

        float timeRatio = (float) state.getCurrentTime() / state.getStepTime();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.4, -0.1, 0);
        GlStateManager.scale(0.625f / 24, 0.625f / 24, 0.625f / 24);

        Minecraft.getMinecraft().fontRenderer.drawString(
                "Step " + (state.getCurrentStep() + 1) + " / " + state.getBlueprint().getSteps().size(), 0, 0,
                16777215);
        GlStateManager.popMatrix();

        GlStateManager.translate(0, 0.5, 0.1);
        GlStateManager.disableLighting();
        GlStateManager.resetColor();
        RenderUtil.renderRect(-0.5, 0.35, -0.5 + 3.3 * timeRatio, -0.3, 0, 0.5f, 0, 0.6f);
        GlStateManager.translate(0, 0, -0.1);

        for (int stack = 0; stack < state.getStepStacks().size(); stack++)
        {
            if (stack != 0)
                GlStateManager.translate(0, 0.75, 0);

            GlStateManager.rotate(180, 1, 0, 0);
            RenderUtil.handleRenderItem(state.getStepStacks().get(stack), false);
            GlStateManager.rotate(-180, 1, 0, 0);
            GlStateManager.disableLighting();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.2, 0.15, -.02);
            GlStateManager.scale(0.625f / 48, 0.625f / 48, 0.625f / 48);


            String count = state.getCurrentStacks().get(stack).getCount() - 1 + "/"
                    + state.getStepStacks().get(stack).getCount();
            int color;
            if (state.getCurrentStacks().get(stack).getCount() - 1 == state.getStepStacks().get(stack).getCount())
                color = 61440;
            else if (state.getCurrentStacks().get(stack).getCount() - 1 == 0)
                color = 15728640;
            else
                color = 15767040;

            Minecraft.getMinecraft().fontRenderer.drawString(count,
                    -Minecraft.getMinecraft().fontRenderer.getStringWidth(count) / 2, 0, color);

            GlStateManager.scale(2.5, 2.5, 2.5);
            GlStateManager.translate(8, -9, 0);

            String stackName = state.getStepStacks().get(stack).getDisplayName();
            Minecraft.getMinecraft().fontRenderer.drawString(stackName, 0, 0, color);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

    private static void renderStep(final int step, final List<ItemStack> stackList, final BlueprintState state)
    {
        if (step != 0)
            GlStateManager.translate(0, 1, 0);
        GlStateManager.pushMatrix();

        float timeRatio;

        if (state.getCurrentStep() == step)
            timeRatio = (float) state.getCurrentTime() / state.getStepTime();
        else if (state.getCurrentStep() > step)
            timeRatio = 1;
        else
            timeRatio = 0;
        GlStateManager.translate(0, 0, 0.1);
        GlStateManager.disableLighting();
        GlStateManager.resetColor();
        RenderUtil.renderRect(-0.5, 0.35, -0.5 + 3.3 * timeRatio, -0.3, 0, 0.5f, 0, 0.6f);
        GlStateManager.translate(0, 0, -0.1);

        for (int stack = 0; stack < stackList.size(); stack++)
        {
            if (stack != 0)
                GlStateManager.translate(1, 0, 0);

            GlStateManager.rotate(180, 1, 0, 0);
            RenderUtil.handleRenderItem(stackList.get(stack), false);
            GlStateManager.rotate(-180, 1, 0, 0);

            GlStateManager.disableLighting();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.2, 0.15, -.02);
            GlStateManager.scale(0.625f / 48, 0.625f / 48, 0.625f / 48);

            String count;

            if (step == state.getCurrentStep() && state.getCurrentStacks().size() > stack)
            {
                count = state.getCurrentStacks().get(stack).getCount() - 1 + "/" + state.getStepStacks().get(stack)
                        .getCount();
            }
            else if (step < state.getCurrentStep())
                count = stackList.get(stack).getCount() + "/" + stackList.get(stack).getCount();
            else
                count = "0/" + stackList.get(stack).getCount();

            int color = 15728640;
            if (step == state.getCurrentStep() && state.getCurrentStacks().size() > stack)
            {
                if (state.getCurrentStacks().get(stack).getCount() - 1 == state.getStepStacks().get(stack).getCount())
                    color = 61440;
                else if (state.getCurrentStacks().get(stack).getCount() - 1 == 0)
                    color = 15728640;
            }
            else if (step < state.getCurrentStep())
                color = 61440;

            Minecraft.getMinecraft().fontRenderer.drawString(count,
                    -Minecraft.getMinecraft().fontRenderer.getStringWidth(count) / 2, 0, color);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
