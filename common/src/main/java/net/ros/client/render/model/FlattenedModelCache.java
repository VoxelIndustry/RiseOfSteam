package net.ros.client.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FlattenedModelCache
{
    private static volatile FlattenedModelCache instance = null;

    public static FlattenedModelCache getInstance()
    {
        if (FlattenedModelCache.instance == null)
            synchronized (FlattenedModelCache.class)
            {
                if (FlattenedModelCache.instance == null)
                    FlattenedModelCache.instance = new FlattenedModelCache();
            }
        return FlattenedModelCache.instance;
    }

    private final LoadingCache<IBakedModel, IBakedModel> cache;

    private FlattenedModelCache()
    {
        this.cache = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(10, TimeUnit.MINUTES)
                .build(new CacheLoader<IBakedModel, IBakedModel>()
                {
                    @Override
                    public IBakedModel load(final IBakedModel key)
                    {
                        IBakedModel model = ForgeHooksClient.handleCameraTransforms(key,
                                ItemCameraTransforms.TransformType.GUI, false);

                        if (model.isGui3d() && !model.isBuiltInRenderer())
                        {
                            model = ModelTransformer.transform(model, null, 0, (quad, element, data) ->
                            {
                                if (element.getUsage() == VertexFormatElement.EnumUsage.NORMAL)
                                {
                                    data[0] /= 1.5f;
                                    data[2] *= 1.7f;
                                }
                                return data;
                            });
                        }
                        return model;
                    }
                });
    }

    public IBakedModel getFlattenedModel(final IBakedModel origin)
    {
        try
        {
            return this.cache.get(origin);
        } catch (final ExecutionException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
