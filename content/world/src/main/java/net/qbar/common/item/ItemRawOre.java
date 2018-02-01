package net.qbar.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.QBarConstants;
import net.qbar.common.ore.MineralDensity;
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
        tooltip.add(this.getMineral(stack).getRarity().rarityColor + I18n.format(this.getMineral(stack).getName()));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return this.getUnlocalizedName() + "." + this.getDensity(stack).toString();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return this.getMineral(stack).getRarity();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (this.isInCreativeTab(tab))
        {
            for (QBarMineral ore : QBarOres.MINERALS)
            {
                subItems.add(QBarOres.getRawMineral(ore, MineralDensity.POOR));
                subItems.add(QBarOres.getRawMineral(ore, MineralDensity.NORMAL));
                subItems.add(QBarOres.getRawMineral(ore, MineralDensity.RICH));
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
                this.getVariantModel(this.getMineral(stack).getNameID() + "_" + this.getDensity(stack)));
        super.registerModels();
    }

    @Override
    public boolean hasSpecialModel()
    {
        return true;
    }

    public QBarMineral getMineral(ItemStack stack)
    {
        return QBarOres.MINERALS.get(stack.getItemDamage() / MineralDensity.VALUES.length);
    }

    public MineralDensity getDensity(ItemStack stack)
    {
        return MineralDensity.VALUES[stack.getItemDamage() % MineralDensity.VALUES.length];
    }
}
