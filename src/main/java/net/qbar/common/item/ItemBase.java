package net.qbar.common.item;

import net.minecraft.item.Item;
import net.qbar.QBar;

public class ItemBase extends Item implements IItemModelProvider
{
    public String name;

    public ItemBase(String name)
    {
        this.name = name;
        this.setRegistryName(QBar.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(QBar.TAB_ALL);
    }
}
