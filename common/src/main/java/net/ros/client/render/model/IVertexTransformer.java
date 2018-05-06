package net.ros.client.render.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

@FunctionalInterface
public interface IVertexTransformer
{
    float[] transform(BakedQuad quad, VertexFormatElement element, float... data);
}
