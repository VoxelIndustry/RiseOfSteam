package net.ros.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.ros.common.ROSConstants;

public class BlockBase extends Block
{
    public BlockBase(final String name, final Material material)
    {
        super(material);
        this.setRegistryName(ROSConstants.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(ROSConstants.TAB_ALL);
    }
}
