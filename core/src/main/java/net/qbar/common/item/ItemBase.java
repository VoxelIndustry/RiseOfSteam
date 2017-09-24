package net.qbar.common.item;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.qbar.QBar;

import java.util.HashMap;
import java.util.Map;

public class ItemBase extends Item implements IItemModelProvider
{
    public String name;

    private Map<String, ModelResourceLocation> variants;

    public ItemBase(String name)
    {
        this.name = name;
        this.setRegistryName(QBar.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(QBar.TAB_ALL);

        this.variants = new HashMap<>();
    }

    protected void addVariant(String name, ModelResourceLocation model)
    {
        this.variants.put(name, model);
    }

    protected ModelResourceLocation getVariantModel(String name)
    {
        return this.variants.get(name);
    }

    @Override
    public void registerVariants()
    {
        ModelBakery.registerItemVariants(this,
                variants.values().toArray(new ModelResourceLocation[variants.size()]));
    }
}
