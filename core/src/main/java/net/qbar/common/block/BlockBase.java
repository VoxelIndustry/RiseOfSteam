package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.qbar.QBar;

public class BlockBase extends Block implements INamedBlock
{
    public String name;

    public BlockBase(final String name, final Material material)
    {
        super(material);
        this.name = name;
        this.setRegistryName(QBar.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(QBar.TAB_ALL);
    }

    @Override
    public String getName()
    {
        return this.name;
    }
}
