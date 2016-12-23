package net.qbar.common.fluid;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockQBarFluid extends BlockFluidClassic
{
    public BlockQBarFluid(final Fluid fluid, final Material material, final String name)
    {
        super(fluid, material);
        this.setDensity(fluid.getDensity());
        this.setUnlocalizedName(name);
    }
}
