package net.ros.common.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.ros.common.ROSConstants;
import net.ros.common.item.ItemBase;
import net.ros.common.item.ItemMetal;

import java.util.ArrayList;
import java.util.List;

@ObjectHolder(ROSConstants.MODID)
public class ROSItems
{
    @ObjectHolder("punched_card")
    public static final ItemBase PUNCHED_CARD = null;

    @ObjectHolder("wrench")
    public static final ItemBase WRENCH = null;

    @ObjectHolder("blueprint")
    public static final ItemBase  BLUEPRINT       = null;
    @ObjectHolder("ironrod")
    public static final ItemBase  IRON_ROD        = null;
    @ObjectHolder("metalplate")
    public static final ItemMetal METALPLATE      = null;
    @ObjectHolder("metalgear")
    public static final ItemMetal METALGEAR       = null;
    @ObjectHolder("metalingot")
    public static final ItemMetal METALINGOT      = null;
    @ObjectHolder("metalnugget")
    public static final ItemMetal METALNUGGET     = null;
    @ObjectHolder("solarreflector")
    public static final ItemBase  SOLAR_REFLECTOR = null;

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

    @ObjectHolder("multiblockbox")
    public static final ItemBase MULTIBLOCK_BOX = null;

    @ObjectHolder("steamcapsule")
    public static final ItemBase STEAMCAPSULE     = null;
    @ObjectHolder("steamcapsulex4")
    public static final ItemBase STEAMCAPSULE_X4  = null;
    @ObjectHolder("steamcapsulex6")
    public static final ItemBase STEAMCAPSULE_X6  = null;
    @ObjectHolder("steamcapsulex12")
    public static final ItemBase STEAMCAPSULE_X12 = null;

    @ObjectHolder("itemvalve")
    public static final ItemBase VALVE = null;
    @ObjectHolder("itemgauge")
    public static final ItemBase GAUGE = null;

    public static List<Item> ITEMS;

    public static void init()
    {
        ITEMS = new ArrayList<>();
        ITEMS.addAll(ROSBlocks.BLOCKS.values());
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
