package net.qbar.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.ore.QBarOres;
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
                tooltip.add(QBarOres.getMineralFromName(stack.getTagCompound().getString("ore" + i)).get()
                        .getRarity().rarityColor
                        + StringUtils.capitalize(stack.getTagCompound().getString("density" + i)) + " "
                        + I18n.translateToLocal(stack.getTagCompound().getString("ore" + i)));
        }
    }
}
