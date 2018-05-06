package net.ros.client.render.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

public final class ModelTransformer
{
    public static IBakedModel transform(final IBakedModel model, final IBlockState state, final long rand,
            final IVertexTransformer transformer)
    {
        final MutableBakedModel out = new MutableBakedModel(model);

        for (int i = 0; i <= 6; i++)
        {
            EnumFacing side = (i == 6 ? null : EnumFacing.getFront(i));
            for (BakedQuad quad : model.getQuads(state, side, rand))
            {
                out.addQuad(side, transform(quad, transformer));
            }
        }
        return out;
    }

    private static BakedQuad transform(final BakedQuad quad, final IVertexTransformer transformer)
    {
        final VertexFormat format = quad.getFormat();
        final UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        LightUtil.putBakedQuad(new VertexTransformerWrapper(builder, quad, transformer), quad);
        return builder.build();
    }
}