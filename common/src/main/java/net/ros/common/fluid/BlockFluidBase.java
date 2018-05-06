package net.ros.common.fluid;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.ros.common.ROSConstants;

public class BlockFluidBase extends BlockFluidClassic
{
    public BlockFluidBase(final Fluid fluid, final Material material, final String name)
    {
        super(fluid, material);
        this.setRegistryName(ROSConstants.MODID, name);
        this.setDensity(fluid.getDensity());
        this.setUnlocalizedName(name);
    }
}
