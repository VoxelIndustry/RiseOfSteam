package net.qbar.common.init;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.common.items.ItemPunchedCard;

public class QBarItems
{
    public static final Item itemPunchedCard = new ItemPunchedCard();

    public static final void registerItems()
    {
        QBarItems.registerItem(QBarItems.itemPunchedCard, "punched_card");
    }

    private static final void registerItem(final Item item, final String name)
    {
        item.setRegistryName(name);
        GameRegistry.register(item);
    }
}
