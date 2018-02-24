package net.qbar.common.init;

import net.qbar.common.item.*;

public class MachineItems
{
    public static void init()
    {
        QBarItems.registerItem(new ItemWrench());
        QBarItems.registerItem(new ItemMultiblockBox());
        QBarItems.registerItem(new ItemDrillCoreSample());
        QBarItems.registerItem(new ItemBlueprint());

        QBarItems.registerItem(new ItemBase("redstone_card"));
        QBarItems.registerItem(new ItemBase("ironrod"));
        QBarItems.registerItem(new ItemBase("solarreflector"));
    }
}
