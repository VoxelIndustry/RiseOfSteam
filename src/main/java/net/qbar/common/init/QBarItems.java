package net.qbar.common.init;

import org.apache.commons.lang3.StringUtils;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;
import net.qbar.QBar;
import net.qbar.common.item.ItemBase;
import net.qbar.common.item.ItemBlueprint;
import net.qbar.common.item.ItemPlate;
import net.qbar.common.item.ItemPunchedCard;
import net.qbar.common.item.ItemWrench;
import net.qbar.common.recipe.QBarRecipeHandler;

@ObjectHolder(QBar.MODID)
public class QBarItems
{
    @ObjectHolder("punched_card")
    public static final ItemBase          PUNCHED_CARD = null;

    @ObjectHolder("wrench")
    public static final ItemBase          WRENCH       = null;

    @ObjectHolder("blueprint")
    public static final ItemBase          BLUEPRINT    = null;
    @ObjectHolder("ironrod")
    public static final ItemBase          IRON_ROD     = null;
    @ObjectHolder("metalplate")
    public static final ItemBase          METALPLATE   = null;

    public static final void registerItems()
    {
        QBarItems.registerMetal("iron");
        QBarItems.registerMetal("gold");

        QBarItems.registerItem(new ItemPunchedCard());
        QBarItems.registerItem(new ItemWrench());

        QBarItems.registerItem(new ItemBlueprint());
        QBarItems.registerItem(new ItemBase("ironrod"));
        QBarItems.registerItem(new ItemPlate());
    }

    private static final void registerItem(final ItemBase item)
    {
        item.setRegistryName(QBar.MODID, item.name);
        GameRegistry.register(item);

        QBar.proxy.registerItemRenderer(item, 0, item.name);
    }

    private static final void registerMetal(final String metalName)
    {
        if (OreDictionary.doesOreNameExist("ingot" + StringUtils.capitalize(metalName)))
            QBarRecipeHandler.metals.add(metalName);
    }
}
