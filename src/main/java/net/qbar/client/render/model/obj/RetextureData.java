package net.qbar.client.render.model.obj;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

public class RetextureData
{
    private final ResourceLocation             originalModel;
    private final ImmutableMap<String, String> replacedTextures;

    public RetextureData(ResourceLocation originalModel, ImmutableMap<String, String> replacedTextures)
    {
        this.originalModel = originalModel;
        this.replacedTextures = replacedTextures;
    }

    public ResourceLocation getOriginalModel()
    {
        return originalModel;
    }

    public ImmutableMap<String, String> getReplacedTextures()
    {
        return replacedTextures;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RetextureData that = (RetextureData) o;

        if (originalModel != null ? !originalModel.equals(that.originalModel) : that.originalModel != null)
            return false;
        return replacedTextures != null ? replacedTextures.equals(that.replacedTextures)
                : that.replacedTextures == null;
    }

    @Override
    public int hashCode()
    {
        int result = originalModel != null ? originalModel.hashCode() : 0;
        result = 31 * result + (replacedTextures != null ? replacedTextures.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "RetextureData{" + "originalModel=" + originalModel + ", replacedTextures=" + replacedTextures + '}';
    }
}
