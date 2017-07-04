package net.qbar.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.ore.QBarOre;
import net.qbar.common.ore.QBarOres;

import java.util.List;

public class ItemRawOre extends ItemBase
{
    public ItemRawOre()
    {
        super("rawore");

        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ore"))
            tooltip.add(QBarOres.getOreFromName(stack.getTagCompound().getString("ore")).get().getRarity().rarityColor
                    + I18n.translateToLocal(stack.getTagCompound().getString("ore")));
    }

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
            return QBarOres.getOreFromName(stack.getTagCompound().getString("ore")).get().getRarity();
        return EnumRarity.COMMON;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (tab == this.getCreativeTab())
        {
            for (QBarOre ore : QBarOres.ORES)
            {
                ItemStack stack = new ItemStack(this);
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setString("ore", ore.getName());
                stack.getTagCompound().setString("density", "normal");
                subItems.add(stack);
            }
        }
    }
}
