package net.ros.client.render.model.obj;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

@Builder
@Getter
public class RetexturedModelData
{
    private String              modelName;
    private ResourceLocation    sourceModel;
    @Singular("texture")
    private Map<String, String> textureMap;
}
