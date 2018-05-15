package net.ros.client.render.model.obj;

import com.google.common.collect.ImmutableMap;
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

    private final Map<String, RetextureData> reTexturedMap = new HashMap<>();

    public void addDomain(String domain)
    {
        enabledDomains.add(domain.toLowerCase());
        ROSConstants.LOGGER.info("Domain registered for ROSOBJLoader: " + domain.toLowerCase());
    }

    public void addRetexturedModel(String modelName, ResourceLocation sourceModel, String[] matKeys, String[] textures)
    {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < matKeys.length; i++)
            map.put(matKeys[i].startsWith("#") ? matKeys[i] : "#" + matKeys[i], textures[i]);

        this.reTexturedMap.put(modelName, new RetextureData(sourceModel, ImmutableMap.copyOf(map)));
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation)
    {
        return enabledDomains.contains(modelLocation.getResourceDomain())
                && modelLocation.getResourcePath().endsWith(".mwm");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception
    {
        ResourceLocation file = new ResourceLocation(modelLocation.getResourceDomain(),
                modelLocation.getResourcePath());
        if (!cache.containsKey(file))
        {
            String fileName = file.getResourcePath().substring(file.getResourcePath().lastIndexOf("/") + 1,
                    file.getResourcePath().length());
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
