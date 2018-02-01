package net.qbar.common.init;

import net.qbar.common.item.ItemMetal;
import net.qbar.common.item.ItemMixedRawOre;
import net.qbar.common.item.ItemRawOre;
import net.qbar.common.item.ItemSludge;
import net.qbar.common.recipe.MaterialShape;

public class WorldItems
{
    public static void init()
    {
        QBarItems.registerItem(new ItemMetal(MaterialShape.PLATE));
        QBarItems.registerItem(new ItemMetal(MaterialShape.GEAR));
        QBarItems.registerItem(new ItemMetal(MaterialShape.INGOT));
        QBarItems.registerItem(new ItemMetal(MaterialShape.NUGGET));

        QBarItems.registerItem(new ItemSludge("mineralsludge"));
        QBarItems.registerItem(new ItemSludge("compressedmineralsludge"));
        QBarItems.registerItem(new ItemRawOre());
        QBarItems.registerItem(new ItemMixedRawOre());
    }
}
