package net.ros.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ros.common.ore.Ores;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;

public class ItemMixedRawOre extends ItemBase
{
    private static NumberFormat percentFormatter = NumberFormat.getPercentInstance();

    static
    {
        percentFormatter.setMinimumFractionDigits(1);
    }

    public ItemMixedRawOre()
    {
        super("mixedrawore");

        this.setCreativeTab(null);
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("oreCount"))
        {
            for (int i = 0; i < stack.getTagCompound().getInteger("oreCount"); i++)
                tooltip.add(Ores.getMineralFromName(stack.getTagCompound().getString("ore" + i)).get()
                        .getRarity().color
                        + StringUtils.capitalize(stack.getTagCompound().getString("density" + i)) + " "
                        + I18n.format(stack.getTagCompound().getString("ore" + i)));
        }
    }
}
