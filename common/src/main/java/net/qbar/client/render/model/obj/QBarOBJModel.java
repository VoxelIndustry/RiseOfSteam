package net.qbar.client.render.model.obj;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.IModelState;
import net.qbar.common.QBarConstants;

import java.lang.reflect.Field;
import java.util.function.Function;

public class QBarOBJModel extends OBJModel
{
    protected final ResourceLocation modelLocation;

    public QBarOBJModel(MaterialLibrary matLib, ResourceLocation modelLocation)
    {
        super(matLib, modelLocation);

        this.modelLocation = modelLocation;
    }

    public QBarOBJModel(MaterialLibrary matLib, ResourceLocation modelLocation, Object data)
    {
        this(matLib, modelLocation);

        this.setCustomData(data);
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
        builder.put(ModelLoader.White.LOCATION.toString(), ModelLoader.White.INSTANCE);
        TextureAtlasSprite missing = bakedTextureGetter.apply(new ResourceLocation("missingno"));
        for (String materialKey : this.getMatLib().getMaterialNames())
        {
            if (this.getMatLib().getMaterial(materialKey).getTexture().getTextureLocation().getResourcePath()
                    .startsWith("#"))
            {
                QBarConstants.LOGGER.error("QBarOBJLoader: Unresolved texture '%s' for obj model '%s' "
                        + this.getMatLib().getMaterial(materialKey).getTexture().getTextureLocation().getResourcePath()
                        + " " + this.modelLocation);
                builder.put(materialKey, missing);
            }
            else
                builder.put(materialKey, bakedTextureGetter
                        .apply(this.getMatLib().getMaterial(materialKey).getTexture().getTextureLocation()));
        }
        builder.put("missingno", missing);
        return new QBarBakedOBJModel(this, state, format, builder.build());
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData)
    {
        QBarOBJModel ret = new QBarOBJModel(this.getMatLib(), getResourceLocation(), getCustomData());
        return ret;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures)
    {
        QBarOBJModel ret = new QBarOBJModel(this.getMatLib().makeLibWithReplacements(textures), getResourceLocation()
                , getCustomData());
        return ret;
    }

    static Field f_modelLocation;

    public ResourceLocation getResourceLocation()
    {
        try
        {
            if (f_modelLocation == null)
            {
                f_modelLocation = OBJModel.class.getDeclaredField("modelLocation");
                f_modelLocation.setAccessible(true);
            }
            if (f_modelLocation != null)
                return (ResourceLocation) f_modelLocation.get(this);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    static Field f_customData;

    public Object getCustomData()
    {
        try
        {
            if (f_customData == null)
            {
                f_customData = OBJModel.class.getDeclaredField("customData");
                f_customData.setAccessible(true);
            }
            if (f_customData != null)
                return f_customData.get(this);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void setCustomData(Object data)
    {
        try
        {
            if (f_customData == null)
            {
                f_customData = OBJModel.class.getDeclaredField("customData");
                f_customData.setAccessible(true);
            }
            if (f_customData != null)
                f_customData.set(this, data);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
