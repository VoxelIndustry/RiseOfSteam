package net.qbar.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.ore.QBarOre;
import net.qbar.common.ore.QBarOres;
import net.qbar.common.ore.SludgeData;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class ItemSludge extends ItemBase
{
    private static NumberFormat percentFormatter = NumberFormat.getPercentInstance();

    static
    {
        percentFormatter.setMinimumFractionDigits(1);
    }

    public ItemSludge(String name)
    {
        super(name);

        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("sludgeData"))
        {
            SludgeData data = SludgeData.fromNBT(stack.getTagCompound().getCompoundTag("sludgeData"));

            for (Map.Entry<QBarOre, Float> ore : data.getOres().entrySet())
                tooltip.add(ore.getKey().getRarity().rarityColor + I18n.translateToLocal(ore.getKey().getName()) + " "
                        + percentFormatter.format(ore.getValue()));
        }
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (tab == this.getCreativeTab())
        {
            for (QBarOre ore : QBarOres.ORES)
            {
                ItemStack stack = new ItemStack(item);

                NBTTagCompound data = SludgeData.builder().ore(ore, 1f).build().writeToNBT(new NBTTagCompound());
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag("sludgeData", data);
                subItems.add(stack);
            }
        }
    }
}
