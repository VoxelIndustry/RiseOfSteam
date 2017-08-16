package net.qbar.common.init;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.qbar.QBar;
import net.qbar.common.block.*;
import net.qbar.common.block.creative.BlockCreativeSteamGenerator;
import net.qbar.common.block.item.ItemBlockMetadata;
import net.qbar.common.block.item.ItemBlockVeinOre;
import net.qbar.common.multiblock.*;
import net.qbar.common.ore.QBarOres;
import net.qbar.common.tile.TileFluidPipe;
import net.qbar.common.tile.TileSteamPipe;
import net.qbar.common.tile.TileStructure;
import net.qbar.common.tile.creative.TileCreativeSteamGenerator;
import net.qbar.common.tile.machine.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@ObjectHolder(QBar.MODID)
public class QBarBlocks
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
    @ObjectHolder("offshorepump")
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
    public static final BlockVeinOre            IRON_NICKEL_ORE      = null;
    @ObjectHolder("ironcopperore")
    public static final BlockVeinOre            IRON_COPPER_ORE      = null;
    @ObjectHolder("tinore")
    public static final BlockVeinOre            TIN_ORE              = null;
    @ObjectHolder("ironzincore")
    public static final BlockVeinOre            IRON_ZINC_ORE        = null;
    @ObjectHolder("goldore")
    public static final BlockVeinOre            GOLD_ORE             = null;
    @ObjectHolder("redstoneore")
    public static final BlockVeinOre            REDSTONE_ORE         = null;
    @ObjectHolder("oredirt")
    public static final BlockOreDirt            ORE_DIRT             = null;
    @ObjectHolder("oreclay")
    public static final BlockOreClay            ORE_CLAY             = null;
    @ObjectHolder("oresand")
    public static final BlockOreSand            ORE_SAND             = null;
    @ObjectHolder("orestone")
    public static final BlockOreStone           ORE_STONE            = null;
    @ObjectHolder("energizedtallgrass")
    public static final BlockEnergizedTallGrass ENERGIZED_TALL_GRASS = null;

    @ObjectHolder("blockmetal")
    public static final BlockMetal METALBLOCK = null;

    public static Map<Block, ItemBlock> BLOCKS;

    public static void init()
    {
        BLOCKS = new LinkedHashMap<>();

        registerBlock(new BlockMultiblockMachine<>("keypunch", Material.IRON, Multiblocks.KEYPUNCH, TileKeypunch::new, TileKeypunch.class));
        registerBlock(new BlockTank("fluidtank_small", Multiblocks.SMALL_FLUID_TANK, Fluid.BUCKET_VOLUME * 48, 0));
        registerBlock(new BlockTank("fluidtank_medium", Multiblocks.MEDIUM_FLUID_TANK, Fluid.BUCKET_VOLUME * 128, 1));
        registerBlock(new BlockTank("fluidtank_big", Multiblocks.BIG_FLUID_TANK, Fluid.BUCKET_VOLUME * 432, 2));
        registerBlock(new BlockSolidBoiler());
        registerBlock(new BlockFluidPipe(), block -> new ItemBlockMetadata(block, "valve"));
        registerBlock(new BlockSteamPipe(), block -> new ItemBlockMetadata(block, "valve"));
        registerBlock(new BlockFluidPump());
        registerBlock(new BlockOffshorePump());
        registerBlock(
                new BlockMultiblockMachine<>("assembler", Material.IRON, Multiblocks.ASSEMBLER, TileAssembler::new, TileAssembler.class));
        registerBlock(new BlockBelt());
        registerBlock(new BlockExtractor(), block -> new ItemBlockMetadata(block, "filter"));
        registerBlock(new BlockSplitter(), block -> new ItemBlockMetadata(block, "filter"));
        registerBlock(new BlockCreativeSteamGenerator());
        registerBlock(new BlockStructure());
        registerBlock(new BlockMultiblockMachine<>("steamfurnacemk1", Material.IRON, Multiblocks.STEAM_FURNACE_MK1,
                TileSteamFurnace::new, TileSteamFurnace.class));
        registerBlock(new BlockSolarBoiler());
        registerBlock(new BlockSolarMirror());
        registerBlock(new BlockMultiblockMachine<>("rollingmill", Material.IRON, Multiblocks.ROLLING_MILL,
                TileRollingMill::new, TileRollingMill.class));
        registerBlock(new BlockLiquidBoiler());
        registerBlock(new BlockMultiblockMachine<>("steamfurnacemk2", Material.IRON, Multiblocks.STEAM_FURNACE_MK2,
                TileSteamFurnaceMK2::new, TileSteamFurnaceMK2.class));
        registerBlock(
                new BlockMultiblockMachine<>("orewasher", Material.IRON, Multiblocks.ORE_WASHER, TileOreWasher::new, TileOreWasher.class));
        registerBlock(new BlockMultiblockMachine<>("sortingmachine", Material.IRON, Multiblocks.SORTING_MACHINE,
                TileSortingMachine::new, TileSortingMachine.class));
        registerBlock(new BlockMultiblockMachine<>("smallminingdrill", Material.IRON, Multiblocks.SMALL_MINING_DRILL,
                TileSmallMiningDrill::new, TileSmallMiningDrill.class));
        registerBlock(new BlockMultiblockMachine<>("tinyminingdrill", Material.IRON, Multiblocks.TINY_MINING_DRILL,
                TileTinyMiningDrill::new, TileTinyMiningDrill.class));
        registerBlock(new BlockMultiblockMachine<>("alloycauldron", Material.IRON, Multiblocks.ALLOY_CAULDRON,
                TileAlloyCauldron::new, TileAlloyCauldron.class));

        registerBlock(new BlockVeinOre.Builder("ironnickelore")
                .addContent(QBarOres.PENTLANDITE)
                .addContent(QBarOres.GARNIERITE)
                .addContent(QBarOres.LATERITE)
                .create(), ItemBlockVeinOre::new);
        registerBlock(new BlockVeinOre.Builder("ironcopperore")
                .addContent(QBarOres.CHALCOPYRITE)
                .addContent(QBarOres.TETRAHEDRITE)
                .addContent(QBarOres.MALACHITE)
                .create(), ItemBlockVeinOre::new);
        registerBlock(new BlockVeinOre.Builder("tinore")
                .addContent(QBarOres.CASSITERITE)
                .addContent(QBarOres.TEALLITE)
                .create(), ItemBlockVeinOre::new);
        registerBlock(new BlockVeinOre.Builder("ironzincore")
                .addContent(QBarOres.SPHALERITE)
                .create(), ItemBlockVeinOre::new);
        registerBlock(new BlockVeinOre.Builder("goldore")
                .addContent(QBarOres.GOLD_ORE)
                .create(), ItemBlockVeinOre::new);
        registerBlock(new BlockVeinOre.Builder("redstoneore")
                .addContent(QBarOres.REDSTONE_ORE)
                .create(), ItemBlockVeinOre::new);

        registerBlock(new BlockOreSand("oresand"), block -> new ItemBlockMetadata(block, "copper_sand", "tin_sand").setFirstVariation(true));
        registerBlock(new BlockOreDirt("oredirt"), block -> new ItemBlockMetadata(block, "iron_dirt").setFirstVariation(true));
        registerBlock(new BlockOreClay("oreclay"), block -> new ItemBlockMetadata(block, "tin_clay").setFirstVariation(true));
        registerBlock(new BlockOreStone("orestone"), block -> new ItemBlockMetadata(block, "gold_rock").setFirstVariation(true));
        registerBlock(new BlockEnergizedTallGrass("energizedtallgrass"),
                block -> new ItemBlockMetadata(block, "dead_bush", "tall_grass", "fern").setFirstVariation(true));

        registerBlock(new BlockMetal(), block ->
                new ItemBlockMetadata(block, BlockMetal.VARIANTS.getAllowedValues().toArray(new String[BlockMetal.VARIANTS.getAllowedValues().size()])).setFirstVariation(true));

        QBarBlocks.registerTile(TileTank.class, "tank");
        QBarBlocks.registerTile(TileKeypunch.class, "keypunch");
        QBarBlocks.registerTile(TileSolidBoiler.class, "boiler");
        QBarBlocks.registerTile(TileFluidPipe.class, "fluidpipe");
        QBarBlocks.registerTile(TileSteamPipe.class, "steampipe");
        QBarBlocks.registerTile(TileFluidPump.class, "fluidpump");
        QBarBlocks.registerTile(TileOffshorePump.class, "offshore_pump");
        QBarBlocks.registerTile(TileAssembler.class, "assembler");
        QBarBlocks.registerTile(TileBelt.class, "belt");
        QBarBlocks.registerTile(TileExtractor.class, "itemextractor");
        QBarBlocks.registerTile(TileSplitter.class, "itemsplitter");
        QBarBlocks.registerTile(TileMultiblockGag.class, "multiblockgag");
        QBarBlocks.registerTile(TileCreativeSteamGenerator.class, "creative_steam_generator");
        QBarBlocks.registerTile(TileStructure.class, "structure");
        QBarBlocks.registerTile(TileSteamFurnace.class, "steamfurnace");
        QBarBlocks.registerTile(TileSolarBoiler.class, "solarboiler");
        QBarBlocks.registerTile(TileSolarMirror.class, "solarmirror");
        QBarBlocks.registerTile(TileRollingMill.class, "rollingmill");
        QBarBlocks.registerTile(TileLiquidBoiler.class, "liquidfuelboiler");
        QBarBlocks.registerTile(TileSteamFurnaceMK2.class, "steamfurnacemk2");
        QBarBlocks.registerTile(TileOreWasher.class, "orewasher");
        QBarBlocks.registerTile(TileSortingMachine.class, "sortingmachine");
        QBarBlocks.registerTile(TileSmallMiningDrill.class, "smallminingdrill");
        QBarBlocks.registerTile(TileTinyMiningDrill.class, "tinyminingdrill");
        QBarBlocks.registerTile(TileAlloyCauldron.class, "alloycauldron");
    }

    @SubscribeEvent
    public void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(BLOCKS.keySet().toArray(new Block[BLOCKS.size()]));
    }

    static <T extends Block & INamedBlock> void registerBlock(final T block)
    {
        if (block instanceof BlockMultiblockBase)
            QBarBlocks.registerBlock(block, ItemBlockMultiblockBase::new);
        else
            QBarBlocks.registerBlock(block, ItemBlock::new);
    }

    private static <T extends Block & INamedBlock> void registerBlock(final T block,
                                                                      final Function<T, ItemBlock> supplier)
    {
        final ItemBlock supplied = supplier.apply(block);
        supplied.setRegistryName(block.getRegistryName());

        BLOCKS.put(block, supplied);
    }

    private static void registerTile(final Class<? extends TileEntity> c, final String name)
    {
        GameRegistry.registerTileEntity(c, QBar.MODID + ":" + name);
    }
}