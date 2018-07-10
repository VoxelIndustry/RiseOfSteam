package net.ros.common.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ros.common.ROSConstants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockFluidBase extends BlockFluidClassic
{
    private List<String> informations = new ArrayList<>();

    public BlockFluidBase(final Fluid fluid, final Material material, final String name)
    {
        super(fluid, material);
        this.setRegistryName(ROSConstants.MODID, name);
        this.setDensity(fluid.getDensity());
        this.setUnlocalizedName(name);
    }

    public void addInformation(String information)
    {
        this.informations.add(information);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.addAll(this.informations);

        super.addInformation(stack, player, tooltip, advanced);
    }
}
