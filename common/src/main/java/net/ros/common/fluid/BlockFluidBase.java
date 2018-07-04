package net.ros.common.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.ros.common.ROSConstants;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockFluidBase extends BlockFluidClassic
{
    private List<Pair<ITooltipFlag, String>> informations;

    public BlockFluidBase(final Fluid fluid, final Material material, final String name)
    {
        super(fluid, material);
        this.setRegistryName(ROSConstants.MODID, name);
        this.setDensity(fluid.getDensity());
        this.setUnlocalizedName(name);

        this.informations = new ArrayList<>();
    }

    public void addInformation(String information)
    {
        this.addInformation(information, ITooltipFlag.TooltipFlags.NORMAL);
    }

    public void addInformation(String information, ITooltipFlag advanced)
    {
        this.informations.add(Pair.of(advanced, information));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        this.informations.forEach(pair ->
        {
            if (pair.getKey() == ITooltipFlag.TooltipFlags.ADVANCED && advanced != ITooltipFlag.TooltipFlags.ADVANCED)
                return;
            tooltip.add(pair.getValue());
        });

        super.addInformation(stack, player, tooltip, advanced);
    }
}
