package net.qbar.common.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.qbar.common.QBarConstants;
import net.qbar.common.item.ItemBase;

import java.util.ArrayList;
import java.util.List;

@ObjectHolder(QBarConstants.MODID)
public class QBarItems
{
    @ObjectHolder("punched_card")
    public static final ItemBase PUNCHED_CARD = null;

    @ObjectHolder("wrench")
    public static final ItemBase WRENCH = null;

    @ObjectHolder("blueprint")
    public static final ItemBase BLUEPRINT       = null;
    @ObjectHolder("ironrod")
    public static final ItemBase IRON_ROD        = null;
    @ObjectHolder("metalplate")
    public static final ItemBase METALPLATE      = null;
    @ObjectHolder("metalgear")
    public static final ItemBase METALGEAR       = null;
    @ObjectHolder("metalingot")
    public static final ItemBase METALINGOT      = null;
    @ObjectHolder("solarreflector")
    public static final ItemBase SOLAR_REFLECTOR = null;

    @ObjectHolder("mineralsludge")
    public static final ItemBase MINERAL_SLUDGE            = null;
    @ObjectHolder("compressedmineralsludge")
    public static final ItemBase COMPRESSED_MINERAL_SLUDGE = null;
    @ObjectHolder("mixedrawore")
    public static final ItemBase MIXED_RAW_ORE             = null;
    @ObjectHolder("rawore")
    public static final ItemBase RAW_ORE                   = null;

    @ObjectHolder("drillcoresample")
    public static final ItemBase DRILL_CORE_SAMPLE = null;

    @ObjectHolder("gearbox")
    public static final ItemBase GEARBOX        = null;
    @ObjectHolder("logicbox")
    public static final ItemBase LOGICBOX       = null;
    @ObjectHolder("multiblockbox")
    public static final ItemBase MULTIBLOCK_BOX = null;

    protected static List<Item> ITEMS;

    public static void init()
    {
        ITEMS = new ArrayList<>();
        ITEMS.addAll(QBarBlocks.BLOCKS.values());
    }

    @SubscribeEvent
    public void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(ITEMS.toArray(new Item[ITEMS.size()]));
    }

    static void registerItem(final ItemBase item)
    {
        ITEMS.add(item);
    }
}
