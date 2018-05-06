package net.ros.common.init;

import net.ros.common.item.*;
import net.ros.common.item.ItemBlueprint;
import net.ros.common.item.ItemDrillCoreSample;
import net.ros.common.item.ItemWrench;

public class MachineItems
{
    public static void init()
    {
        ROSItems.registerItem(new ItemWrench());
        ROSItems.registerItem(new ItemMultiblockBox());
        ROSItems.registerItem(new ItemDrillCoreSample());
        ROSItems.registerItem(new ItemBlueprint());

        ROSItems.registerItem(new ItemBase("redstone_card"));
        ROSItems.registerItem(new ItemBase("ironrod"));
        ROSItems.registerItem(new ItemBase("solarreflector"));
    }
}
