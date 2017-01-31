package net.qbar.common.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.qbar.QBar;
import net.qbar.common.item.ItemBase;
import net.qbar.common.item.ItemPunchedCard;

@ObjectHolder(QBar.MODID)
public class QBarItems
{
    @ObjectHolder("punched_card")
    public static final ItemBase PUNCHED_CARD = null;

    public static final void registerItems()
    {
        QBarItems.registerItem(new ItemPunchedCard());
    }

    private static final void registerItem(final ItemBase item)
    {
        item.setRegistryName(item.name);
        GameRegistry.register(item);
    }
}
