package net.ros.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ros.common.ROSConstants;
import net.ros.common.ore.Slag;

public class ItemSlag extends ItemBase
{
    public ItemSlag()
    {
        super("slag");

        this.setHasSubtypes(true);
    }

    public Slag getVariant(int meta)
    {
        return Slag.values()[meta];
    }

    public int getIndex(Slag variant)
    {
        return variant.ordinal();
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        return this.getTranslationKey() + "." + this.getVariant(stack.getMetadata()).toString();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        Slag variant = getVariant(stack.getMetadata());
        return variant == Slag.SPARKLING || variant == Slag.SHINY ? EnumRarity.UNCOMMON :
                EnumRarity.COMMON;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (this.isInCreativeTab(tab))
        {
            Slag[] values = Slag.values();
            for (int i = 0; i < values.length; i++)
                subItems.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public void registerVariants()
    {
        for (Slag variant : Slag.values())
        {
            this.addVariant(variant.toString(), new ModelResourceLocation(ROSConstants.MODID +
                    ":slag_" + variant.toString(), "inventory"));
        }
        super.registerVariants();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels()
    {
        ModelLoader.setCustomMeshDefinition(this, stack ->
                this.getVariantModel(this.getVariant(stack.getMetadata()).toString()));
        super.registerModels();
    }

    @Override
    public boolean hasSpecialModel()
    {
        return true;
    }
}
