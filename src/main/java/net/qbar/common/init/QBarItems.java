package net.qbar.common.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.common.item.ItemBase;
import net.qbar.common.item.ItemPunchedCard;

public class QBarItems
{
    public static final ItemBase itemPunchedCard = new ItemPunchedCard();

    public static final void registerItems()
    {
        QBarItems.registerItem(QBarItems.itemPunchedCard);
    }

    private static final void registerItem(final ItemBase item)
    {
        item.setRegistryName(item.name);
        GameRegistry.register(item);
    }
}
