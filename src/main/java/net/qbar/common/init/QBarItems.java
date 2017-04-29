package net.qbar.common.init;

import net.qbar.common.item.*;
import org.apache.commons.lang3.StringUtils;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;
import net.qbar.QBar;
import net.qbar.common.recipe.QBarRecipeHandler;

@ObjectHolder(QBar.MODID)
public class QBarItems
{
    @ObjectHolder("punched_card")
    public static final ItemBase PUNCHED_CARD    = null;

    @ObjectHolder("wrench")
    public static final ItemBase WRENCH          = null;

    @ObjectHolder("blueprint")
    public static final ItemBase BLUEPRINT       = null;
    @ObjectHolder("ironrod")
    public static final ItemBase IRON_ROD        = null;
    @ObjectHolder("metalplate")
    public static final ItemBase METALPLATE      = null;
    @ObjectHolder("solarreflector")
    public static final ItemBase SOLAR_REFLECTOR = null;

    @ObjectHolder("mineralsludge")
    public static final ItemBase MINERAL_SLUDGE = null;
    @ObjectHolder("compressedmineralsludge")
    public static final ItemBase COMPRESSED_MINERAL_SLUDGE = null;
    @ObjectHolder("mixedrawore")
    public static final ItemBase MIXED_RAW_ORE = null;
    @ObjectHolder("rawore")
    public static final ItemBase RAW_ORE = null;

    public static final void registerItems()
    {
        QBarItems.registerMetal("iron");
        QBarItems.registerMetal("gold");

        QBarItems.registerItem(new ItemPunchedCard());
        QBarItems.registerItem(new ItemWrench());

        QBarItems.registerItem(new ItemBlueprint());
        QBarItems.registerItem(new ItemBase("ironrod"));
        QBarItems.registerItem(new ItemPlate());
        QBarItems.registerItem(new ItemBase("solarreflector"));

        QBarItems.registerItem(new ItemSludge("mineralsludge"));
        QBarItems.registerItem(new ItemSludge("compressedmineralsludge"));
        QBarItems.registerItem(new ItemRawOre());
        QBarItems.registerItem(new ItemMixedRawOre());
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
