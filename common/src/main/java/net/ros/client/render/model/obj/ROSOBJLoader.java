package net.ros.client.render.model.obj;

import lombok.Getter;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.ros.common.ROSConstants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum ROSOBJLoader implements ICustomModelLoader
{
    INSTANCE;

    private final Set<String>                      enabledDomains = new HashSet<>();
    private final Map<ResourceLocation, IModel>    cache          = new HashMap<>();
    private final Map<ResourceLocation, Exception> errors         = new HashMap<>();

    @Getter
    private final Map<String, RetextureData> reTexturedMap = new HashMap<>();

    public void addDomain(String domain)
    {
        enabledDomains.add(domain.toLowerCase());
        ROSConstants.LOGGER.info("Domain registered for ROSOBJLoader: " + domain.toLowerCase());
    }

    public void addRetexturedModel(String modelName, RetextureData data)
    {
        this.reTexturedMap.put(modelName, data);
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation)
    {
        return enabledDomains.contains(modelLocation.getNamespace())
                && modelLocation.getPath().endsWith(".mwm");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception
    {
        ResourceLocation file = new ResourceLocation(modelLocation.getNamespace(),
                modelLocation.getPath());
        if (!cache.containsKey(file))
        {
            String fileName = file.getPath().substring(file.getPath().lastIndexOf("/") + 1);
            if (fileName.startsWith("_") && this.reTexturedMap.containsKey(fileName))
            {
                cache.put(modelLocation, new RetexturedOBJModel(this.reTexturedMap.get(fileName).getOriginalModel(),
                        this.reTexturedMap.get(fileName).getReplacedTextures()));
            }
            else
            {
                IModel model = OBJLoader.INSTANCE.loadModel(modelLocation);
                if (model instanceof OBJModel)
                {
                    ROSOBJModel obj = new ROSOBJModel(((OBJModel) model).getMatLib(), file);
                    cache.put(modelLocation, obj);
                }
            }
        }
        IModel model = cache.get(file);
        if (model == null)
            return ModelLoaderRegistry.getMissingModel();
        return model;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        cache.clear();
        errors.clear();
    }
}
