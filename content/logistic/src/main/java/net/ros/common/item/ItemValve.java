package net.ros.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.ros.common.ROSConstants;

public class ItemValve extends ItemBase
{
    public ItemValve()
    {
        super("itemvalve");
    }

    @Override
    public void registerVariants()
    {
        this.addVariant("normal", new ModelResourceLocation(ROSConstants.MODID + ":obj/steamvalve.mwm", "inventory"));
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
