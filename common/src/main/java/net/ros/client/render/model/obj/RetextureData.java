package net.ros.client.render.model.obj;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode
@ToString
public class RetextureData
{
    private final ResourceLocation    originalModel;
    private final ImmutableMap<String, String> replacedTextures;

    RetextureData(ResourceLocation originalModel, ImmutableMap<String, String> replacedTextures)
    {
        this.originalModel = originalModel;
        this.replacedTextures = replacedTextures;
    }

    public static RetextureDataBuilder builder()
    {
        return new RetextureDataBuilder();
    }

    public static class RetextureDataBuilder
    {
        private ResourceLocation    originalModel;
        private Map<String, String> replacedTextures;

        RetextureDataBuilder()
        {
            this.replacedTextures = new HashMap<>();
        }

        public RetextureDataBuilder originalModel(ResourceLocation originalModel)
        {
            this.originalModel = originalModel;
            return this;
        }

        public RetextureDataBuilder texture(String textureKey, String textureValue)
        {
            this.replacedTextures.put(textureKey.startsWith("#") ? textureKey : "#" + textureKey, textureValue);
            return this;
        }

        public RetextureData build()
        {
            return new RetextureData(this.originalModel, ImmutableMap.copyOf(this.replacedTextures));
        }
    }
}
