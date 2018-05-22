package net.ros.common.item;

import lombok.Getter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ros.common.ROSConstants;
import net.ros.common.recipe.MaterialShape;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.Metal;

import java.util.List;

public class ItemMetal extends ItemBase
{
    @Getter
    private List<Metal>   metals;
    private MaterialShape shape;

    public ItemMetal(MaterialShape shape)
    {
        super("metal" + shape);

        this.setHasSubtypes(true);
        this.shape = shape;

        this.metals = Materials.metals.getAllMetalForShape(shape);
    }

    public int getMetalMeta(Metal metal)
    {
        if (Materials.metals.containsShape(metal, shape))
            return Materials.metals.indexOf(metal);
        return -1;
    }

    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> list)
    {
        if (this.isInCreativeTab(tab))
        {
            for (int i = 0; i < Materials.metals.size(); i++)
            {
                if (metals.contains(Materials.metals.get(i)))
                    list.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack)
    {
        return this.getUnlocalizedName() + "." + Materials.metals.get(stack.getMetadata());
    }

    @Override
    public void registerVariants()
    {
        this.metals.forEach(metal -> this.addVariant(metal.getName(), new ModelResourceLocation(ROSConstants.MODID + ":"
                + shape.toString() + "_" + metal, "inventory")));
        super.registerVariants();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels()
    {
        ModelLoader.setCustomMeshDefinition(this, stack ->
                this.getVariantModel(Materials.metals.get(stack.getMetadata()).getName()));
        super.registerModels();
    }

    @Override
    public boolean hasSpecialModel()
    {
        return true;
    }
}
