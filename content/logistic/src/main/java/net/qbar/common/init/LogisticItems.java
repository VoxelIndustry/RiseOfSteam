package net.qbar.common.init;

import net.qbar.common.item.ItemPunchedCard;

public class LogisticItems
{
    public static void init()
    {
        QBarItems.registerItem(new ItemPunchedCard());
    }
}
