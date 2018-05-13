package net.ros.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.ros.common.ROSConstants;
import net.ros.common.block.BlockMachineBase;
import net.ros.common.block.BlockMetal;
import net.ros.common.multiblock.BlockMultiblockBase;
import net.ros.common.multiblock.ItemBlockMultiblockBase;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@ObjectHolder(ROSConstants.MODID)
public class ROSBlocks
{
    @ObjectHolder("keypunch")
    public static final BlockMachineBase PUNCHING_MACHINE = null;
    @ObjectHolder("fluidtank")
    public static final BlockMachineBase FLUID_TANK       = null;
    @ObjectHolder("solid_boiler")
    public static final BlockMachineBase SOLID_BOILER     = null;
    @ObjectHolder("fluidpipe")
    public static final BlockMachineBase FLUID_PIPE       = null;
    @ObjectHolder("steampipe")
    public static final BlockMachineBase STEAM_PIPE       = null;
    @ObjectHolder("fluidpump")
    public static final BlockMachineBase FLUID_PUMP       = null;
    @ObjectHolder("offshore_pump")
    public static final BlockMachineBase OFFSHORE_PUMP    = null;
    @ObjectHolder("assembler")
    public static final BlockMachineBase ASSEMBLER        = null;

    // Creative
    @ObjectHolder("creative_steam_generator")
    public static final BlockMachineBase CREATIVE_BOILER = null;

    @ObjectHolder("belt")
    public static final BlockMachineBase BELT           = null;
    @ObjectHolder("itemextractor")
    public static final BlockMachineBase ITEM_EXTRACTOR = null;
    @ObjectHolder("itemsplitter")
    public static final BlockMachineBase ITEM_SPLITTER  = null;

    @ObjectHolder("structure")
    public static final BlockMachineBase STRUCTURE          = null;
    @ObjectHolder("steamfurnace")
    public static final BlockMachineBase STEAM_FURNACE      = null;
    @ObjectHolder("rollingmill")
    public static final BlockMachineBase ROLLING_MILL       = null;
    @ObjectHolder("solar_mirror")
    public static final BlockMachineBase SOLAR_MIRROR       = null;
    @ObjectHolder("solar_boiler")
    public static final BlockMachineBase SOLAR_BOILER       = null;
    @ObjectHolder("liquidfuel_boiler")
    public static final BlockMachineBase LIQUID_FUEL_BOILER = null;
    @ObjectHolder("steamfurnacemk2")
    public static final BlockMachineBase STEAM_FURNACE_MK2  = null;

    @ObjectHolder("orewasher")
    public static final BlockMachineBase ORE_WASHER         = null;
    @ObjectHolder("sortingmachine")
    public static final BlockMachineBase SORTING_MACHINE    = null;
    @ObjectHolder("smallminingdrill")
    public static final BlockMachineBase SMALL_MINING_DRILL = null;
    @ObjectHolder("tinyminingdrill")
    public static final BlockMachineBase TINY_MINING_DRILL  = null;

    @ObjectHolder("ironnickelore")
    public static final Block IRON_NICKEL_ORE      = null;
    @ObjectHolder("ironcopperore")
    public static final Block IRON_COPPER_ORE      = null;
    @ObjectHolder("tinore")
    public static final Block TIN_ORE              = null;
    @ObjectHolder("ironzincore")
    public static final Block IRON_ZINC_ORE        = null;
    @ObjectHolder("goldore")
    public static final Block GOLD_ORE             = null;
    @ObjectHolder("redstoneore")
    public static final Block REDSTONE_ORE         = null;
    @ObjectHolder("oredirt")
    public static final Block ORE_DIRT             = null;
    @ObjectHolder("oreclay")
    public static final Block ORE_CLAY             = null;
    @ObjectHolder("oresand")
    public static final Block ORE_SAND             = null;
    @ObjectHolder("orestone")
    public static final Block ORE_STONE            = null;
    @ObjectHolder("energizedtallgrass")
    public static final Block ENERGIZED_TALL_GRASS = null;

    @ObjectHolder("blockmetal")
    public static final BlockMetal       METALBLOCK         = null;
    @ObjectHolder("blockmetalplate")
    public static final BlockMetal       METALPLATEBLOCK         = null;
    @ObjectHolder("sawmill")
    public static final BlockMachineBase SAWMILL            = null;
    @ObjectHolder("alloycauldron")
    public static final BlockMachineBase ALLOYCAULDRON      = null;
    @ObjectHolder("engineerworkbench")
    public static final BlockMachineBase ENGINEER_WORKBENCH = null;
    @ObjectHolder("engineerstorage")
    public static final BlockMachineBase ENGINEER_STORAGE   = null;
    @ObjectHolder("craftcardlibrary")
    public static final BlockMachineBase CRAFT_CARD_LIBRARY = null;
    @ObjectHolder("blueprintprinter")
    public static final BlockMachineBase BLUEPRINT_PRINTER  = null;

    public static Map<Block, ItemBlock> BLOCKS;

    public static void init()
    {
        BLOCKS = new LinkedHashMap<>();
    }

    @SubscribeEvent
    public void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(BLOCKS.keySet().toArray(new Block[BLOCKS.size()]));
    }

    static <T extends Block> void registerBlock(final T block)
    {
        if (block instanceof BlockMultiblockBase)
            ROSBlocks.registerBlock(block, ItemBlockMultiblockBase::new);
        else
            ROSBlocks.registerBlock(block, ItemBlock::new);
    }

    static <T extends Block> void registerBlock(final T block,
                                                final Function<T, ItemBlock> supplier)
    {
        final ItemBlock supplied = supplier.apply(block);
        supplied.setRegistryName(block.getRegistryName());

        BLOCKS.put(block, supplied);
    }

    static void registerTile(final Class<? extends TileEntity> c, final String name)
    {
        GameRegistry.registerTileEntity(c, ROSConstants.MODID + ":" + name);
    }
}