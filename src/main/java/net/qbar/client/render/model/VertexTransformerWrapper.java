package net.qbar.client.render.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;

final class VertexTransformerWrapper implements IVertexConsumer
{
    private final IVertexConsumer    parent;
    private final BakedQuad          parentQuad;
    private final VertexFormat       format;
    private final IVertexTransformer transformer;

    public VertexTransformerWrapper(final IVertexConsumer parent, final BakedQuad parentQuad,
                                    final IVertexTransformer transformer)
    {
        this.parent = parent;
        this.parentQuad = parentQuad;
        this.format = parent.getVertexFormat();
        this.transformer = transformer;
    }

    @Override
    public VertexFormat getVertexFormat()
    {
        return this.format;
    }

    @Override
    public void setQuadTint(final int tint)
    {
        this.parent.setQuadTint(tint);
    }

    @Override
    public void setQuadOrientation(final EnumFacing orientation)
    {
        this.parent.setQuadOrientation(orientation);
    }

    @Override
    public void setApplyDiffuseLighting(final boolean diffuse)
    {
        this.parent.setApplyDiffuseLighting(diffuse);
    }

    @Override
    public void setTexture(final TextureAtlasSprite texture)
    {
        this.parent.setTexture(texture);
    }

    @Override
    public void put(final int elementId, final float... data)
    {
        final VertexFormatElement element = this.format.getElement(elementId);
        this.parent.put(elementId, this.transformer.transform(this.parentQuad, element, data));
    }
}