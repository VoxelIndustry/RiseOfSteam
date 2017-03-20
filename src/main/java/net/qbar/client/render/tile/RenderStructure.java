package net.qbar.client.render.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.qbar.QBar;
import net.qbar.client.render.RenderUtil;
import net.qbar.common.tile.TileStructure;

public class RenderStructure extends TileEntitySpecialRenderer<TileStructure>
{
    public static BlockRendererDispatcher blockRender;

    public RenderStructure()
    {
    }

    @Override
    public void renderTileEntityAt(final TileStructure structure, final double x, final double y, final double z,
            final float partialTicks, final int destroyStage)
    {
        if (RenderStructure.blockRender == null)
            RenderStructure.blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();

        if (structure.getBlueprint() != null)
        {
            final int l = structure.getWorld().getCombinedLight(structure.getPos().offset(EnumFacing.UP),
                    structure.getWorld().getSkylightSubtracted());
            final int j = l % 65536;
            final int k = l / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);

            final IBlockState state = Block.getBlockFromName(QBar.MODID + ":" + structure.getBlueprint().getName())
                    .getStateFromMeta(structure.getMeta());

            if (structure.getBlueprintState().getMultiblockStep() != null)
                RenderUtil.renderMultiblock(state, x, y, z, RenderStructure.blockRender, structure.getQuads(),
                        structure.getBlueprintState().getMultiblockStep().getOpaqueState());
            else
                RenderUtil.renderMultiblock(state, x, y, z, RenderStructure.blockRender);
        }
    }
}