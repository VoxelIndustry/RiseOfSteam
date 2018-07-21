package net.ros.common.world;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.ros.common.block.BlockVeinOre;
import net.ros.common.init.ROSBlocks;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static net.minecraft.init.Biomes.*;
import static net.ros.common.ore.Ores.*;
import static net.ros.common.world.EVeinForm.UPWARD;
import static net.ros.common.world.EVeinHeapForm.PLATES;
import static net.ros.common.world.EVeinHeapForm.SPHERES;

public class OreVeins
{
    public static List<OreVeinDescriptor> VEINS;

    public static OreVeinDescriptor IRON_NICKEL;
    public static OreVeinDescriptor IRON_COPPER;
    public static OreVeinDescriptor TIN;
    public static OreVeinDescriptor IRON_ZINC;
    public static OreVeinDescriptor GOLD;
    public static OreVeinDescriptor REDSTONE;

    public static VeinMarker IRON_DIRT_MARKER;
    public static VeinMarker COPPER_SAND_MARKER;
    public static VeinMarker TIN_CLAY_MARKER;
    public static VeinMarker TIN_SAND_MARKER;
    public static VeinMarker GOLD_ROCK_MARKER;
    public static VeinMarker REDSTONE_GRASS_MARKER;

    public static void initVeins()
    {
        initMarkers();

        IRON_NICKEL = new OreVeinDescriptor("iron-nickel").veinForm(UPWARD).heapForm(PLATES)
                .heapQty(4).heapDensity(0.8f).heapSize(5).rarity(0.002f).heightRange(4, 64).richChance(0.1f, 0.8f)
                .poorChance(0, 0.8f)
                .content(((BlockVeinOre) ROSBlocks.IRON_NICKEL_ORE).getStateFromOre(PENTLANDITE), 0.3f)
                .content(((BlockVeinOre) ROSBlocks.IRON_NICKEL_ORE).getStateFromOre(GARNIERITE), 0.3f)
                .content(((BlockVeinOre) ROSBlocks.IRON_NICKEL_ORE).getStateFromOre(LATERITE), 0.4f)
                .biomes(BiomeMatcher.fromBiomes(JUNGLE, JUNGLE_HILLS, JUNGLE_EDGE, SAVANNA, SAVANNA_PLATEAU))
                .marker(IRON_DIRT_MARKER).createBlockSupplier();

        IRON_COPPER = new OreVeinDescriptor("iron-copper").veinForm(UPWARD).heapForm(PLATES)
                .heapQty(4).heapDensity(0.7f).heapSize(5).rarity(0.002f).heightRange(4, 64).richChance(0.1f, 0.8f)
                .poorChance(0, 0.8f)
                .content(((BlockVeinOre) ROSBlocks.IRON_COPPER_ORE).getStateFromOre(CHALCOPYRITE), 0.3f)
                .content(((BlockVeinOre) ROSBlocks.IRON_COPPER_ORE).getStateFromOre(TETRAHEDRITE), 0.3f)
                .content(((BlockVeinOre) ROSBlocks.IRON_COPPER_ORE).getStateFromOre(MALACHITE), 0.4f)
                .biomes(BiomeMatcher.fromBiomes(RIVER, FROZEN_RIVER, SWAMPLAND,
                        MUTATED_SWAMPLAND, FOREST_HILLS, BIRCH_FOREST_HILLS, JUNGLE_HILLS,
                        EXTREME_HILLS, TAIGA_HILLS))
                .marker(COPPER_SAND_MARKER).createBlockSupplier();

        TIN = new OreVeinDescriptor("tin").veinForm(UPWARD).heapForm(PLATES).heapQty(5)
                .heapDensity(0.5f).heapSize(7).rarity(0.001f).heightRange(4, 64).richChance(0.1f, 0.8f).poorChance
                        (0, 0.8f)
                .content(((BlockVeinOre) ROSBlocks.TIN_ORE).getStateFromOre(CASSITERITE), 1)
                .content(((BlockVeinOre) ROSBlocks.TIN_ORE).getStateFromOre(TEALLITE), 1)
                .biomes(BiomeMatcher.fromBiomes(FOREST, FOREST_HILLS, BIRCH_FOREST_HILLS,
                        BIRCH_FOREST, EXTREME_HILLS, EXTREME_HILLS_EDGE,
                        EXTREME_HILLS_WITH_TREES))
                .marker(TIN_CLAY_MARKER).marker(TIN_SAND_MARKER).createBlockSupplier();

        IRON_ZINC = new OreVeinDescriptor("iron-zinc").veinForm(UPWARD).heapForm(PLATES)
                .heapQty(4).heapDensity(0.75f).heapSize(6).rarity(0.0005f).heightRange(4, 64).richChance(0.1f, 0.8f)
                .poorChance(0, 0.8f)
                .content(((BlockVeinOre) ROSBlocks.IRON_ZINC_ORE).getStateFromOre(SPHALERITE), 1)
                .biomes(BiomeMatcher.fromBiomes(FOREST, FOREST_HILLS, BIRCH_FOREST,
                        BIRCH_FOREST_HILLS, PLAINS, OCEAN, DEEP_OCEAN, FROZEN_OCEAN,
                        EXTREME_HILLS, EXTREME_HILLS_WITH_TREES, EXTREME_HILLS_EDGE))
                .createBlockSupplier();

        GOLD = new OreVeinDescriptor("gold").veinForm(UPWARD).heapForm(EVeinHeapForm.SCATTERED).heapQty(2)
                .heapDensity(0.1f).heapSize(15).rarity(0.0001f).heightRange(4, 64).richChance(0, 0.6f).poorChance
                        (0.2f, 1f)
                .content(((BlockVeinOre) ROSBlocks.GOLD_ORE).getStateFromOre(GOLD_ORE), 1)
                .biomes(BiomeMatcher.WILDCARD)
                .marker(GOLD_ROCK_MARKER).createBlockSupplier();

        REDSTONE = new OreVeinDescriptor("redstone").veinForm(UPWARD).heapForm(SPHERES)
                .heapQty(3).heapDensity(0.3f).heapSize(12).rarity(0.0001f).heightRange(4, 64).richChance(0.1f, 0.8f)
                .poorChance(0, 0.8f)
                .content(((BlockVeinOre) ROSBlocks.REDSTONE_ORE).getStateFromOre(REDSTONE_ORE), 1)
                .biomes(BiomeMatcher.fromBiomes(PLAINS, SWAMPLAND, FOREST, FOREST_HILLS,
                        BIRCH_FOREST, BIRCH_FOREST_HILLS))
                .marker(REDSTONE_GRASS_MARKER).createBlockSupplier();

        VEINS = Lists.newArrayList(IRON_NICKEL, IRON_COPPER, TIN, IRON_ZINC, GOLD, REDSTONE);
    }

    private static void initMarkers()
    {
        VeinBlockSupplier ironBlockSupplier = (rand, veinSize, centerOffset) -> FeatureGenerator.randomState(rand, Lists
                .newArrayList(Pair.of(ROSBlocks.ORE_DIRT.getStateFromMeta(0), 1f)));
        IRON_DIRT_MARKER = (world, pos, actualHeapSize, heapSize) -> FeatureGenerator.generatePlate(world, world
                        .getTopSolidOrLiquidBlock(pos),
                ironBlockSupplier, (int) (4 * ((float) actualHeapSize / heapSize)), 3, 0.8f,
                OreGenerator.instance().DECORATION_PREDICATE);

        VeinBlockSupplier copperBlockSupplier = (rand, veinSize, centerOffset) -> FeatureGenerator.randomState(rand,
                Lists
                        .newArrayList(Pair.of(ROSBlocks.ORE_SAND.getStateFromMeta(0), 1f)));
        COPPER_SAND_MARKER = (world, pos, actualHeapSize, heapSize) -> FeatureGenerator.generatePlate(world, world
                        .getTopSolidOrLiquidBlock(pos),
                copperBlockSupplier, (int) (4 * ((float) actualHeapSize / heapSize)), 3, 0.8f,
                OreGenerator.instance().DECORATION_PREDICATE);

        VeinBlockSupplier tinClayBlockSupplier = (rand, veinSize, centerOffset) -> FeatureGenerator.randomState(rand,
                Lists
                        .newArrayList(Pair.of(ROSBlocks.ORE_CLAY.getStateFromMeta(0), 1f)));
        TIN_CLAY_MARKER = (world, pos, actualHeapSize, heapSize) -> FeatureGenerator.generatePlate(world, world
                        .getTopSolidOrLiquidBlock(pos),
                tinClayBlockSupplier, (int) (3 * ((float) actualHeapSize / heapSize)), 3, 0.8f,
                OreGenerator.instance().DECORATION_PREDICATE);

        VeinBlockSupplier tinSandBlockSupplier = (rand, veinSize, centerOffset) -> FeatureGenerator.randomState(rand,
                Lists
                        .newArrayList(Pair.of(ROSBlocks.ORE_SAND.getStateFromMeta(1), 1f)));
        TIN_SAND_MARKER = (world, pos, actualHeapSize, heapSize) -> FeatureGenerator.generatePlate(world, world
                        .getTopSolidOrLiquidBlock(pos),
                tinSandBlockSupplier, (int) (3 * ((float) actualHeapSize / heapSize)), 3, 0.8f,
                OreGenerator.instance().DECORATION_PREDICATE);

        VeinBlockSupplier goldBlockSupplier = (rand, veinSize, centerOffset) -> FeatureGenerator.randomState(rand, Lists
                .newArrayList(Pair.of(ROSBlocks.ORE_STONE.getStateFromMeta(0), 1f)));
        GOLD_ROCK_MARKER = (world, pos, actualHeapSize, heapSize) -> FeatureGenerator.generateSphere(world, world
                        .getTopSolidOrLiquidBlock(pos),
                goldBlockSupplier, (int) (3 * ((float) actualHeapSize / heapSize)), 0.4f,
                OreGenerator.instance().DECORATION_PREDICATE);

        List<Pair<IBlockState, Float>> redstoneStateList = Lists.newArrayList(
                Pair.of(ROSBlocks.ENERGIZED_TALL_GRASS.getStateFromMeta(1), 0.7f),
                Pair.of(ROSBlocks.ENERGIZED_TALL_GRASS.getStateFromMeta(2), 1f));
        REDSTONE_GRASS_MARKER = (world, pos, actualHeapSize, heapSize) -> FeatureGenerator.generateTallGrassPatch(world,
                world.getTopSolidOrLiquidBlock(pos), redstoneStateList, (int) (6 * ((float) actualHeapSize /
                        heapSize)), 0.5f);
    }
}
