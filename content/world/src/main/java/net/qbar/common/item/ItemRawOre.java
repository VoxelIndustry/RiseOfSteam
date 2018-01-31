package net.qbar.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.QBarConstants;
import net.qbar.common.ore.QBarMineral;
import net.qbar.common.ore.QBarOres;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRawOre extends ItemBase
{
    public ItemRawOre()
    {
        super("rawore");

        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ore"))
        {
            QBarOres.getMineralFromName(stack.getTagCompound().getString("ore")).ifPresent(mineral ->
                    tooltip.add(mineral.getRarity().rarityColor + I18n.format(stack.getTagCompound().getString("ore")
                    )));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (stack.hasTagCompound())
            return this.getUnlocalizedName() + "." + stack.getTagCompound().getString("density");
        else
            return this.getUnlocalizedName();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        if (stack.hasTagCompound())
            return QBarOres.getMineralFromName(stack.getTagCompound().getString("ore")).get().getRarity();
        return EnumRarity.COMMON;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (this.isInCreativeTab(tab))
        {
            for (QBarMineral ore : QBarOres.MINERALS)
            {
                ItemStack stack = new ItemStack(this);
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setString("ore", ore.getName());
                stack.getTagCompound().setString("density", "normal");
                subItems.add(stack);
            }
        }
    }

    @Override
    public void registerVariants()
    {
        QBarOres.MINERALS.forEach(mineral -> this.addVariant(mineral.getNameID() + "_poor",
                new ModelResourceLocation(QBarConstants.MODID + ":raw_" + mineral.getNameID() + "_poor", "inventory")));
        QBarOres.MINERALS.forEach(mineral -> this.addVariant(mineral.getNameID() + "_normal",
                new ModelResourceLocation(QBarConstants.MODID + ":raw_" + mineral.getNameID() + "_normal",
                        "inventory")));
        QBarOres.MINERALS.forEach(mineral -> this.addVariant(mineral.getNameID() + "_rich",
                new ModelResourceLocation(QBarConstants.MODID + ":raw_" + mineral.getNameID() + "_rich", "inventory")));
        super.registerVariants();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels()
    {
        ModelLoader.setCustomMeshDefinition(this, stack ->
                this.getVariantModel(stack.getTagCompound().getString("ore").substring(4) + "_" +
                        stack.getTagCompound().getString("density")));
        super.registerModels();
    }

    @Override
    public boolean hasSpecialModel()
    {
        return true;
    }
}
