package net.ros.common.item;

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
import net.ros.common.ROSConstants;
import net.ros.common.ore.MineralDensity;
import net.ros.common.ore.Mineral;
import net.ros.common.ore.Ores;

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
            for (Mineral ore : Ores.MINERALS)
            {
                subItems.add(Ores.getRawMineral(ore, MineralDensity.POOR));
                subItems.add(Ores.getRawMineral(ore, MineralDensity.NORMAL));
                subItems.add(Ores.getRawMineral(ore, MineralDensity.RICH));
            }
        }
    }

    @Override
    public void registerVariants()
    {
        Ores.MINERALS.forEach(mineral -> this.addVariant(mineral.getNameID() + "_poor",
                new ModelResourceLocation(ROSConstants.MODID + ":raw_" + mineral.getNameID() + "_poor", "inventory")));
        Ores.MINERALS.forEach(mineral -> this.addVariant(mineral.getNameID() + "_normal",
                new ModelResourceLocation(ROSConstants.MODID + ":raw_" + mineral.getNameID() + "_normal",
                        "inventory")));
        Ores.MINERALS.forEach(mineral -> this.addVariant(mineral.getNameID() + "_rich",
                new ModelResourceLocation(ROSConstants.MODID + ":raw_" + mineral.getNameID() + "_rich", "inventory")));
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

    public Mineral getMineral(ItemStack stack)
    {
        return Ores.MINERALS.get(stack.getItemDamage() / MineralDensity.VALUES.length);
    }

    public MineralDensity getDensity(ItemStack stack)
    {
        return MineralDensity.VALUES[stack.getItemDamage() % MineralDensity.VALUES.length];
    }
}
