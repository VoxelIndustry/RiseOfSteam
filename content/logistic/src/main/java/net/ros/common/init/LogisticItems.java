package net.ros.common.init;

import net.ros.common.item.*;

public class LogisticItems
{
    public static void init()
    {
        ROSItems.registerItem(new ItemPunchedCard());
        ROSItems.registerItem(new ItemSteamCapsule("steamcapsule", 250, 1.5f));
        ROSItems.registerItem(new ItemSteamCapsule("steamcapsulex4", 1000, 1.5f));
        ROSItems.registerItem(new ItemSteamCapsule("steamcapsulex6", 1500, 1.5f));
        ROSItems.registerItem(new ItemSteamCapsule("steamcapsulex12", 3000, 1.5f));

        ROSItems.registerItem(new ItemValve());
        ROSItems.registerItem(new ItemGauge());
    }
}
