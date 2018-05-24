package net.ros.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.ros.common.ROSConstants;

public class ItemGauge extends ItemBase
{
    public ItemGauge()
    {
        super("itemgauge");
    }

    @Override
    public void registerVariants()
    {
        this.addVariant("normal", new ModelResourceLocation(ROSConstants.MODID + ":obj/steamgauge.obj", "inventory"));
        super.registerVariants();
    }

    @Override
    public void registerModels()
    {
        ModelLoader.setCustomMeshDefinition(this, stack ->
                this.getVariantModel("normal"));

        super.registerModels();
    }

    @Override
    public boolean hasSpecialModel()
    {
        return true;
    }
}
