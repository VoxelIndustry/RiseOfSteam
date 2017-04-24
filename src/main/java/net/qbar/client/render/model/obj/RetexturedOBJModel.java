package net.qbar.client.render.model.obj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.IModelState;

public class RetexturedOBJModel implements IModel
{
    ResourceLocation             sourceModel;
    ImmutableMap<String, String> replacedTextures;

    public RetexturedOBJModel(ResourceLocation sourceModel, ImmutableMap<String, String> replacedTextures)
    {
        this.sourceModel = sourceModel;
        this.replacedTextures = replacedTextures;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return ImmutableList.of(sourceModel);
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        try
        {
            List<ResourceLocation> ret = new ArrayList<>(ModelLoaderRegistry.getModel(sourceModel).getTextures());
            for (String tex : replacedTextures.values())
                ret.add(new ResourceLocation(tex));
            return ret;
        } catch (Exception e)
            {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        try
        {
            IModel model = ModelLoaderRegistry.getModel(sourceModel);
            if (model instanceof QBarOBJModel)
            {
                model = ((QBarOBJModel) model).retexture(replacedTextures);

                return model.bake(state, format, bakedTextureGetter);
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public IModelState getDefaultState()
    {
        return null;
    }
}
