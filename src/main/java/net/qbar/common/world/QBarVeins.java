package net.qbar.common.world;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.qbar.common.init.QBarBlocks;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class QBarVeins
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

        IRON_NICKEL = new OreVeinDescriptor("iron-nickel").veinForm(EVeinForm.FLAT).heapForm(EVeinHeapForm.PLATES)
                .heapQty(5).heapDensity(0.8f).heapSize(10).rarity(0.002f).heightRange(4, 64)
                .content(QBarBlocks.IRON_NICKEL_ORE.getStateFromOre("pentlandite"), 0.3f)
                .content(QBarBlocks.IRON_NICKEL_ORE.getStateFromOre("garnierite"), 0.3f)
                .content(QBarBlocks.IRON_NICKEL_ORE.getStateFromOre("laterite"), 0.4f)
                .biomes(BiomeMatcher.fromBiomes(Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.SAVANNA,
                        Biomes.SAVANNA_PLATEAU))
                .marker(IRON_DIRT_MARKER);

        IRON_COPPER = new OreVeinDescriptor("iron-copper").veinForm(EVeinForm.FLAT).heapForm(EVeinHeapForm.PLATES)
                .heapQty(5).heapDensity(0.7f).heapSize(10).rarity(0.002f).heightRange(4, 64)
                .content(QBarBlocks.IRON_COPPER_ORE.getStateFromOre("chalcopyrite"), 0.3f)
                .content(QBarBlocks.IRON_COPPER_ORE.getStateFromOre("tetrahedrite"), 0.3f)
                .content(QBarBlocks.IRON_COPPER_ORE.getStateFromOre("malachite"), 0.4f)
                .biomes(BiomeMatcher.fromBiomes(Biomes.RIVER, Biomes.FROZEN_RIVER, Biomes.SWAMPLAND,
                        Biomes.MUTATED_SWAMPLAND, Biomes.FOREST_HILLS, Biomes.BIRCH_FOREST_HILLS, Biomes.JUNGLE_HILLS,
                        Biomes.EXTREME_HILLS, Biomes.TAIGA_HILLS))
                .marker(COPPER_SAND_MARKER);

        TIN = new OreVeinDescriptor("tin").veinForm(EVeinForm.UPWARD).heapForm(EVeinHeapForm.PLATES).heapQty(5)
                .heapDensity(0.5f).heapSize(15).rarity(0.001f).heightRange(4, 64)
                .content(QBarBlocks.TIN_ORE.getStateFromOre("cassiterite"), 1)
                .biomes(BiomeMatcher.fromBiomes(Biomes.FOREST, Biomes.FOREST_HILLS, Biomes.BIRCH_FOREST_HILLS,
                        Biomes.BIRCH_FOREST, Biomes.EXTREME_HILLS, Biomes.EXTREME_HILLS_EDGE,
                        Biomes.EXTREME_HILLS_WITH_TREES))
                .marker(TIN_CLAY_MARKER).marker(TIN_SAND_MARKER);

        IRON_ZINC = new OreVeinDescriptor("iron-zinc").veinForm(EVeinForm.FLAT).heapForm(EVeinHeapForm.PLATES)
                .heapQty(6).heapDensity(0.75f).heapSize(13).rarity(0.0005f).heightRange(4, 64)
                .content(QBarBlocks.IRON_ZINC_ORE.getStateFromOre("sphalerite"), 1)
                .biomes(BiomeMatcher.fromBiomes(Biomes.FOREST, Biomes.FOREST_HILLS, Biomes.BIRCH_FOREST,
                        Biomes.BIRCH_FOREST_HILLS, Biomes.PLAINS, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.FROZEN_OCEAN,
                        Biomes.EXTREME_HILLS, Biomes.EXTREME_HILLS_WITH_TREES, Biomes.EXTREME_HILLS_EDGE));

        GOLD = new OreVeinDescriptor("gold").veinForm(EVeinForm.SCATTERED).heapForm(EVeinHeapForm.SHATTERED).heapQty(1)
                .heapDensity(0.1f).heapSize(20).rarity(0.0001f).heightRange(4, 64)
                .content(QBarBlocks.GOLD_ORE.getStateFromOre("gold"), 1).biomes(BiomeMatcher.WILDCARD).marker(GOLD_ROCK_MARKER);

        REDSTONE = new OreVeinDescriptor("redstone").veinForm(EVeinForm.SCATTERED).heapForm(EVeinHeapForm.SPHERES)
                .heapQty(5).heapDensity(0.3f).heapSize(25).rarity(0.0001f).heightRange(4, 64)
                .content(QBarBlocks.REDSTONE_ORE.getStateFromOre("redstone"), 1)
                .biomes(BiomeMatcher.fromBiomes(Biomes.PLAINS, Biomes.SWAMPLAND, Biomes.FOREST, Biomes.FOREST_HILLS,
                        Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS))
                .marker(REDSTONE_GRASS_MARKER);

        VEINS = Lists.newArrayList(IRON_NICKEL, IRON_COPPER, TIN, IRON_ZINC, GOLD, REDSTONE);
    }

    private static void initMarkers()
    {
        List<Pair<IBlockState, Float>> ironStateList = Lists
                .newArrayList(Pair.of(QBarBlocks.ORE_DIRT.getStateFromMeta(0), 1f));
        IRON_DIRT_MARKER = (world, pos) -> FeatureGenerator.generatePlate(world, world.getTopSolidOrLiquidBlock(pos),
                ironStateList, 4, 3, 0.8f, QBarOreGenerator.instance().DECORATION_PREDICATE);

        List<Pair<IBlockState, Float>> copperStateList = Lists
                .newArrayList(Pair.of(QBarBlocks.ORE_SAND.getStateFromMeta(0), 1f));
        COPPER_SAND_MARKER = (world, pos) -> FeatureGenerator.generatePlate(world, world.getTopSolidOrLiquidBlock(pos),
                copperStateList, 4, 3, 0.8f, QBarOreGenerator.instance().DECORATION_PREDICATE);

        List<Pair<IBlockState, Float>> tinClayStateList = Lists
                .newArrayList(Pair.of(QBarBlocks.ORE_CLAY.getStateFromMeta(0), 1f));
        TIN_CLAY_MARKER = (world, pos) -> FeatureGenerator.generatePlate(world, world.getTopSolidOrLiquidBlock(pos),
                tinClayStateList, 3, 3, 0.8f, QBarOreGenerator.instance().DECORATION_PREDICATE);

        List<Pair<IBlockState, Float>> tinSandStateList = Lists
                .newArrayList(Pair.of(QBarBlocks.ORE_SAND.getStateFromMeta(1), 1f));
        TIN_SAND_MARKER = (world, pos) -> FeatureGenerator.generatePlate(world, world.getTopSolidOrLiquidBlock(pos),
                tinSandStateList, 3, 3, 0.8f, QBarOreGenerator.instance().DECORATION_PREDICATE);

        List<Pair<IBlockState, Float>> goldStateList = Lists
                .newArrayList(Pair.of(QBarBlocks.ORE_STONE.getStateFromMeta(0), 1f));
        GOLD_ROCK_MARKER = (world, pos) -> FeatureGenerator.generateSphere(world, world.getTopSolidOrLiquidBlock(pos),
                goldStateList, 3, 0.4f, QBarOreGenerator.instance().DECORATION_PREDICATE);

        List<Pair<IBlockState, Float>> redstoneStateList = Lists.newArrayList(
                Pair.of(QBarBlocks.ENERGIZED_TALL_GRASS.getStateFromMeta(1), 0.7f),
                Pair.of(QBarBlocks.ENERGIZED_TALL_GRASS.getStateFromMeta(2), 1f));
        REDSTONE_GRASS_MARKER = (world, pos) -> FeatureGenerator.generateTallGrassPatch(world,
                world.getTopSolidOrLiquidBlock(pos), redstoneStateList, 6, 0.5f);
    }
}
