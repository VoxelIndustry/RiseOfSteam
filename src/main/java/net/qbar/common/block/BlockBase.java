package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.qbar.QBar;

public class BlockBase extends Block
{
	public String name;
	public BlockBase(String name, Material material)
	{
		super(material);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setCreativeTab(QBar.TAB_ALL);
	}
}
