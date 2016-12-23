package net.qbar.common.item;

import net.minecraft.item.Item;
import net.qbar.QBar;

public class ItemBase extends Item
{
	public String name;

	public ItemBase(String name)
	{
		this.name = name;
		this.setUnlocalizedName(name);
		this.setCreativeTab(QBar.TAB_ALL);
	}
}
