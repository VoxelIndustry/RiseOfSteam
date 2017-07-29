package net.qbar.common.fluid;

import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.qbar.QBar;
import net.qbar.common.block.INamedBlock;

public class BlockQBarFluid extends BlockFluidClassic implements INamedBlock
{
    @Getter
    private String name;

    public BlockQBarFluid(final Fluid fluid, final Material material, final String name)
    {
        super(fluid, material);
        this.setRegistryName(QBar.MODID, name);
        this.setDensity(fluid.getDensity());
        this.setUnlocalizedName(name);

        this.name = name;
    }
}
