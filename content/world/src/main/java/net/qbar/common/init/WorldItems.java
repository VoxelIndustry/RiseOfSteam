package net.qbar.common.init;

import net.minecraftforge.oredict.OreDictionary;
import net.qbar.common.item.ItemMetal;
import net.qbar.common.item.ItemMixedRawOre;
import net.qbar.common.item.ItemRawOre;
import net.qbar.common.item.ItemSludge;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class WorldItems
{
    public static void init()
    {
        QBarItems.registerItem(new ItemMetal("plate", m -> true));
        QBarItems.registerItem(new ItemMetal("gear",
                Arrays.asList("iron", "copper", "bronze", "brass", "steel")::contains));
        QBarItems.registerItem(new ItemMetal("ingot", metal ->
                !OreDictionary.doesOreNameExist("ingot" + StringUtils.capitalize(metal))));

        QBarItems.registerItem(new ItemSludge("mineralsludge"));
        QBarItems.registerItem(new ItemSludge("compressedmineralsludge"));
        QBarItems.registerItem(new ItemRawOre());
        QBarItems.registerItem(new ItemMixedRawOre());
    }
}
