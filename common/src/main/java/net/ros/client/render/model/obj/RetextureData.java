package net.ros.client.render.model.obj;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.util.ResourceLocation;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RetextureData
{
    private final ResourceLocation             originalModel;
    private final ImmutableMap<String, String> replacedTextures;
}
