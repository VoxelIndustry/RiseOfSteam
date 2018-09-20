package net.ros.common.init;

import net.ros.common.item.*;

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
