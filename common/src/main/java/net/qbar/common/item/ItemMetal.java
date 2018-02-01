package net.qbar.common.item;

import lombok.Getter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.QBarConstants;
import net.qbar.common.recipe.MaterialShape;
import net.qbar.common.recipe.QBarMaterials;

import java.util.List;

public class ItemMetal extends ItemBase
{
    @Getter
    private List<String>  metals;
    private MaterialShape shape;

    public ItemMetal(MaterialShape shape)
    {
        super("metal" + shape);

        this.setHasSubtypes(true);
        this.shape = shape;

        this.metals = QBarMaterials.metals.getAllMetalForShape(shape);
    }

    public int getMetalMeta(String metal)
    {
        if (QBarMaterials.metals.containsShape(metal, shape))
            return QBarMaterials.metals.indexOf(metal);
        return -1;
    }

    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> list)
    {
        if (this.isInCreativeTab(tab))
        {
            for (int i = 0; i < QBarMaterials.metals.size(); i++)
            {
                if (metals.contains(QBarMaterials.metals.get(i)))
                    list.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack)
    {
        return this.getUnlocalizedName() + "." + QBarMaterials.metals.get(stack.getMetadata());
    }

    @Override
    public void registerVariants()
    {
        this.metals.forEach(metal -> this.addVariant(metal, new ModelResourceLocation(QBarConstants.MODID + ":"
                + shape.toString() + "_" + metal, "inventory")));
        super.registerVariants();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels()
    {
        ModelLoader.setCustomMeshDefinition(this, stack ->
                this.getVariantModel(QBarMaterials.metals.get(stack.getMetadata())));
        super.registerModels();
    }

    @Override
    public boolean hasSpecialModel()
    {
        return true;
    }
}
