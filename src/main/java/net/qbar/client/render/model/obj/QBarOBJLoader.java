package net.qbar.client.render.model.obj;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.cache.LoadingCache;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.qbar.QBar;

public enum QBarOBJLoader implements ICustomModelLoader
{
    INSTANCE;

    private IResourceManager                          manager;
    private final Set<String>                         enabledDomains = new HashSet<>();
    private final Map<ResourceLocation, QBarOBJModel> cache          = new HashMap<>();
    private final Map<ResourceLocation, Exception>    errors         = new HashMap<>();

    public void addDomain(String domain)
    {
        enabledDomains.add(domain.toLowerCase());
        QBar.logger.info("Domain registered for QBarOBJLoader: " + domain.toLowerCase());
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
            IModel model = OBJLoader.INSTANCE.loadModel(modelLocation);
            if (model instanceof OBJModel)
            {
                QBarOBJModel obj = new QBarOBJModel(((OBJModel) model).getMatLib(), file);
                cache.put(modelLocation, obj);
            }
        }
        QBarOBJModel model = cache.get(file);
        if (model == null)
            return ModelLoaderRegistry.getMissingModel();
        return model;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.manager = resourceManager;
        cache.clear();
        errors.clear();
    }
}
