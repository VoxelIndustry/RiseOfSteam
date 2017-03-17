package net.qbar.client.render.tile;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraftforge.client.model.animation.FastTESR;
import net.qbar.QBar;
import net.qbar.client.render.RenderUtil;
import net.qbar.common.tile.TileStructure;

public class RenderStructure extends FastTESR<TileStructure>
{
    private static BlockRendererDispatcher blockRender;

    @Override
    public void renderTileEntityFast(final TileStructure structure, final double x, final double y, final double z,
            final float partialTicks, final int destroyStage, final VertexBuffer renderer)
    {
        if (RenderStructure.blockRender == null)
            RenderStructure.blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();
        RenderUtil.renderMultiblock(
                Block.getBlockFromName(QBar.MODID + ":" + structure.getBlueprint().getName())
                        .getStateFromMeta(structure.getMeta()),
                structure.getPos(), x, y, z, structure.getWorld(), RenderStructure.blockRender);
    }
}