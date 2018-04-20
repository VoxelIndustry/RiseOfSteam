package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.qbar.common.QBarConstants;

public class BlockBase extends Block
{
    public BlockBase(final String name, final Material material)
    {
        super(material);
        this.setRegistryName(QBarConstants.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(QBarConstants.TAB_ALL);
    }
}
