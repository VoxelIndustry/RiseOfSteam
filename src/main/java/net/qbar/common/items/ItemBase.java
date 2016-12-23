package net.qbar.common.items;

import net.minecraft.item.Item;
import net.qbar.QBar;

/**
 * Created by mantal on 23/12/2016.
 */
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
