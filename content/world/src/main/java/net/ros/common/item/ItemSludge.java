package net.ros.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ros.common.ore.Mineral;
import net.ros.common.ore.Ores;
import net.ros.common.ore.SludgeData;

import javax.annotation.Nullable;
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


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("sludgeData"))
        {
            SludgeData data = SludgeData.fromNBT(stack.getTagCompound().getCompoundTag("sludgeData"));

            for (Map.Entry<Mineral, Float> ore : data.getOres().entrySet())
                tooltip.add(ore.getKey().getRarity().rarityColor + I18n.translateToLocal(ore.getKey().getName()) + " "
                        + percentFormatter.format(ore.getValue()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (this.isInCreativeTab(tab))
        {
            for (Mineral ore : Ores.MINERALS)
            {
                ItemStack stack = new ItemStack(this);

                NBTTagCompound data = new SludgeData().addOre(ore, 1f).writeToNBT(new NBTTagCompound());
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag("sludgeData", data);
                subItems.add(stack);
            }
        }
    }
}
