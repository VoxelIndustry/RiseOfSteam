package net.ros.common.init;

import net.ros.common.item.ItemMetal;
import net.ros.common.item.ItemMixedRawOre;
import net.ros.common.item.ItemRawOre;
import net.ros.common.item.ItemSludge;
import net.ros.common.recipe.MaterialShape;

public class WorldItems
{
    public static void init()
    {
        ROSItems.registerItem(new ItemMetal(MaterialShape.PLATE));
        ROSItems.registerItem(new ItemMetal(MaterialShape.GEAR));
        ROSItems.registerItem(new ItemMetal(MaterialShape.INGOT));
        ROSItems.registerItem(new ItemMetal(MaterialShape.NUGGET));

        ROSItems.registerItem(new ItemSludge("mineralsludge"));
        ROSItems.registerItem(new ItemSludge("compressedmineralsludge"));
        ROSItems.registerItem(new ItemRawOre());
        ROSItems.registerItem(new ItemMixedRawOre());
    }
}
