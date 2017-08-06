package net.qbar.common.item;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.qbar.QBar;
import net.qbar.common.recipe.QBarRecipeHandler;

import java.util.HashMap;

public class ItemPlate extends ItemBase
{
    public ItemPlate()
    {
        super("metalplate");
        this.setHasSubtypes(true);
    }

    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> list)
    {
        if (this.isInCreativeTab(tab))
        {
            QBarRecipeHandler.metals.forEach(metal ->
            {
                final ItemStack stack = new ItemStack(this);
                final NBTTagCompound tag = new NBTTagCompound();
                stack.setTagCompound(tag);

                tag.setString("metal", metal);
                list.add(stack);
            });
        }
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("metal"))
            return this.getUnlocalizedName() + "." + stack.getTagCompound().getString("metal");
        return this.getUnlocalizedName();
    }

    @Override
    public void registerModels()
    {
        HashMap<String, ModelResourceLocation> plateModels = new HashMap<>();

        plateModels.put("gold", new ModelResourceLocation(QBar.MODID + ":plate_gold", "inventory"));
        plateModels.put("iron", new ModelResourceLocation(QBar.MODID + ":plate_iron", "inventory"));

        ModelBakery.registerItemVariants(this,
                plateModels.values().toArray(new ModelResourceLocation[plateModels.size()]));

        ModelLoader.setCustomMeshDefinition(this, stack ->
        {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("metal"))
                return plateModels.get(stack.getTagCompound().getString("metal"));
            return new ModelResourceLocation(QBar.MODID + "ironrod");
        });
    }
}
