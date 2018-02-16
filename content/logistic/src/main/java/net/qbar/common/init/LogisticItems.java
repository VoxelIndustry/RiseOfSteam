package net.qbar.common.init;

import net.qbar.common.item.ItemPunchedCard;
import net.qbar.common.item.ItemSteamCapsule;

public class LogisticItems
{
    public static void init()
    {
        QBarItems.registerItem(new ItemPunchedCard());
        QBarItems.registerItem(new ItemSteamCapsule("steamcapsule", 250, 1.5f));
        QBarItems.registerItem(new ItemSteamCapsule("steamcapsulex4", 1000, 1.5f));
        QBarItems.registerItem(new ItemSteamCapsule("steamcapsulex6", 1500, 1.5f));
        QBarItems.registerItem(new ItemSteamCapsule("steamcapsulex12", 3000, 1.5f));
    }
}
