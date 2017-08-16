package net.qbar.common.item;

import lombok.Getter;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.qbar.QBar;
import net.qbar.common.recipe.QBarMaterials;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemMetal extends ItemBase
{
    @Getter
    private List<String> metals;
    private String       type;

    public ItemMetal(String type, Predicate<String> acceptor)
    {
        super("metal" + type);

        this.setHasSubtypes(true);
        this.type = type;

        this.metals = QBarMaterials.metals.stream().filter(acceptor).collect(Collectors.toList());
    }

    public boolean hasMetalVariant(String metal)
    {
        return this.metals.contains(metal);
    }

    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> list)
    {
        if (this.isInCreativeTab(tab))
        {
            this.metals.forEach(metal ->
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
    public void registerVariants()
    {
        this.metals.forEach(metal -> this.addVariant(metal, new ModelResourceLocation(QBar.MODID + ":" + type + "_" + metal, "inventory")));
        super.registerVariants();
    }

    @Override
    public void registerModels()
    {
        ModelLoader.setCustomMeshDefinition(this, stack ->
        {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("metal"))
                return this.getVariantModel(stack.getTagCompound().getString("metal"));
            return new ModelResourceLocation("");
        });

        super.registerModels();
    }

    @Override
    public boolean hasSpecialModel()
    {
        return true;
    }
}
