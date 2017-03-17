package net.qbar.client;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.qbar.client.render.RenderUtil;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.item.ItemBlueprint;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.TileMultiblockGag;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.multiblock.blueprint.BlueprintState;
import net.qbar.common.multiblock.blueprint.Blueprints;
import net.qbar.common.tile.TileStructure;
import net.qbar.common.util.ItemUtils;

public class ClientEventManager
{
    private final BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();

    @SubscribeEvent
    public void onDrawblockHightlight(final DrawBlockHighlightEvent e)
    {
        if (e.getTarget().typeOfHit.equals(RayTraceResult.Type.BLOCK))
        {
            if (e.getPlayer().getHeldItemMainhand().getItem() instanceof ItemBlueprint)
            {
                if (e.getPlayer().getHeldItemMainhand().hasTagCompound()
                        && e.getPlayer().getHeldItemMainhand().getTagCompound().hasKey("blueprint"))
                {
                    final String name = e.getPlayer().getHeldItemMainhand().getTagCompound().getString("blueprint");
                    final Blueprint blueprint = Blueprints.getInstance().getBlueprints()
                            .get(e.getPlayer().getHeldItemMainhand().getTagCompound().getString("blueprint"));
                    final BlockMultiblockBase base = (BlockMultiblockBase) Block.getBlockFromName("qbar:" + name);
                    final World w = Minecraft.getMinecraft().world;
                    if (base != null)
                    {
                        if (base.canPlaceBlockAt(w, e.getTarget().getBlockPos().offset(e.getTarget().sideHit)))
                        {
                            GlStateManager.enableBlend();
                            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                                    GlStateManager.DestFactor.ZERO);
                            GlStateManager.glLineWidth(2.0F);
                            GlStateManager.disableTexture2D();
                            GlStateManager.depthMask(false);
                            final BlockPos pos = e.getTarget().getBlockPos().offset(e.getTarget().sideHit);

                            if (e.getPlayer().world.getWorldBorder().contains(pos))
                            {
                                final double x = e.getPlayer().lastTickPosX
                                        + (e.getPlayer().posX - e.getPlayer().lastTickPosX) * e.getPartialTicks();
                                final double y = e.getPlayer().lastTickPosY
                                        + (e.getPlayer().posY - e.getPlayer().lastTickPosY) * e.getPartialTicks();
                                final double z = e.getPlayer().lastTickPosZ
                                        + (e.getPlayer().posZ - e.getPlayer().lastTickPosZ) * e.getPartialTicks();
                                RenderGlobal.drawSelectionBoundingBox(
                                        base.getDefaultState().getSelectedBoundingBox(e.getPlayer().world, pos)
                                                .expandXyz(0.0020000000949949026D).offset(-x, -y, -z),
                                        0.0F, 0.0F, 0.0F, 0.4F);

                                GlStateManager.depthMask(true);
                                GlStateManager.enableTexture2D();
                                GlStateManager.disableBlend();

                                RenderUtil.renderMultiblock(
                                        base.getStateForPlacement(w, pos, e.getTarget().sideHit, 0, 0, 0, 0,
                                                e.getPlayer(), e.getPlayer().getActiveHand()),
                                        pos, pos.getX() - x, pos.getY() - y, pos.getZ() - z, e.getPlayer().world,
                                        this.blockRender);

                                GlStateManager.pushMatrix();
                                GlStateManager.translate(pos.getX() - x + 0.5, pos.getY() - y, pos.getZ() - z + .5);
                                GlStateManager.rotate(180, 1, 0, 0);
                                GlStateManager.rotate(e.getPlayer().getHorizontalFacing().getHorizontalAngle() - 180, 0,
                                        1, 0);
                                GlStateManager.translate(0.15, -1.4, -.69);
                                GlStateManager.scale(0.625f / 32, 0.625f / 32, 0.625f / 32);
                                GlStateManager.disableLighting();

                                Minecraft.getMinecraft().fontRendererObj.drawString(
                                        String.valueOf(blueprint.getRodAmount()),
                                        -Minecraft.getMinecraft().fontRendererObj
                                                .getStringWidth(String.valueOf(blueprint.getRodAmount())) / 2,
                                        0,
                                        e.getPlayer().capabilities.isCreativeMode || ItemUtils.hasPlayerEnough(
                                                e.getPlayer().inventory, blueprint.getRodStack(), false) ? 38400
                                                        : 9830400);

                                GlStateManager.enableLighting();
                                GlStateManager.popMatrix();

                                GlStateManager.pushMatrix();
                                GlStateManager.translate(pos.getX() - x + 0.5, pos.getY() - y, pos.getZ() - z + .5);

                                GlStateManager.rotate(90, 1, 0, 0);
                                GlStateManager.rotate(-90, 0, 1, 0);
                                GlStateManager.rotate(e.getPlayer().getHorizontalFacing().getHorizontalAngle(), 1, 0,
                                        0);
                                GlStateManager.translate(-1.5, -0.5, 0);

                                RenderUtil.handleRenderItem(blueprint.getRodStack(), false);
                                GlStateManager.popMatrix();
                                e.setCanceled(true);
                            }
                        }
                    }
                }
            }
            else if (e.getPlayer().world.getBlockState(e.getTarget().getBlockPos()).getBlock() == QBarBlocks.STRUCTURE)
            {
                final BlockPos pos = e.getTarget().getBlockPos();
                final TileEntity tile = e.getPlayer().world.getTileEntity(e.getTarget().getBlockPos());

                if (tile != null)
                {
                    TileStructure structure = null;
                    if (tile instanceof TileMultiblockGag)
                        structure = (TileStructure) ((TileMultiblockGag) tile).getCore();
                    else
                        structure = (TileStructure) tile;

                    if (structure != null && structure.getBlueprint() != null)
                    {
                        final double x = e.getPlayer().lastTickPosX
                                + (e.getPlayer().posX - e.getPlayer().lastTickPosX) * e.getPartialTicks();
                        final double y = e.getPlayer().lastTickPosY
                                + (e.getPlayer().posY - e.getPlayer().lastTickPosY) * e.getPartialTicks();
                        final double z = e.getPlayer().lastTickPosZ
                                + (e.getPlayer().posZ - e.getPlayer().lastTickPosZ) * e.getPartialTicks();

                        GlStateManager.pushMatrix();
                        GlStateManager.translate(pos.getX() - x + 0.5, pos.getY() - y, pos.getZ() - z + .5);
                        GlStateManager.rotate(180, 1, 0, 0);
                        GlStateManager.rotate(e.getPlayer().getHorizontalFacing().getHorizontalAngle() - 180, 0, 1, 0);
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(0, -1, -.51);
                        GlStateManager.scale(0.625f / 48, 0.625f / 48, 0.625f / 48);
                        GlStateManager.disableLighting();
                        GlStateManager.disableBlend();

                        final String name = I18n
                                .translateToLocal(Block.getBlockFromName("qbar:" + structure.getBlueprint().getName())
                                        .getUnlocalizedName() + ".name");
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
                                if (step == currentStep)
                                    count = blueprintState.getCurrentStacks().get(stack).getCount() - 1 + "/"
                                            + stackList.get(stack).getCount();
                                else if (step < currentStep)
                                    count = stackList.get(stack).getCount() + "/" + stackList.get(stack).getCount();
                                else
                                    count = "0/" + stackList.get(stack).getCount();
                                Minecraft.getMinecraft().fontRendererObj.drawString(count,
                                        -Minecraft.getMinecraft().fontRendererObj.getStringWidth(count) / 2, 0,
                                        16777215);
                                GlStateManager.popMatrix();
                            }
                            GlStateManager.popMatrix();
                            step++;
                        }
                        GlStateManager.popMatrix();
                        GlStateManager.enableLighting();
                    }
                }
            }
        }
    }

}
