package net.ros.common.init;

import net.ros.common.block.*;
import net.ros.common.block.item.ItemBlockMetadata;
import net.ros.common.block.item.ItemBlockVeinOre;
import net.ros.common.ore.Ores;
import net.ros.common.recipe.MaterialShape;

import static net.ros.common.init.ROSBlocks.registerBlock;

public class WorldBlocks
{
    public static void init()
    {
        registerBlock(new BlockVeinOre.Builder("ironnickelore").addContent(Ores.PENTLANDITE)
                .addContent(Ores.GARNIERITE).addContent(Ores.LATERITE).create(), ItemBlockVeinOre::new);
        registerBlock(
                new BlockVeinOre.Builder("ironcopperore").addContent(Ores.CHALCOPYRITE)
                        .addContent(Ores.TETRAHEDRITE).addContent(Ores.MALACHITE).create(),
                ItemBlockVeinOre::new);
        registerBlock(new BlockVeinOre.Builder("tinore").addContent(Ores.CASSITERITE)
                .addContent(Ores.TEALLITE).create(), ItemBlockVeinOre::new);
        registerBlock(new BlockVeinOre.Builder("ironzincore").addContent(Ores.SPHALERITE).create(),
                ItemBlockVeinOre::new);
        registerBlock(new BlockVeinOre.Builder("goldore").addContent(Ores.GOLD_ORE).create(),
                ItemBlockVeinOre::new);
        registerBlock(new BlockVeinOre.Builder("redstoneore").addContent(Ores.REDSTONE_ORE).create(),
                ItemBlockVeinOre::new);

        registerBlock(new BlockOreSand("oresand"),
                block -> new ItemBlockMetadata(block, "copper_sand", "tin_sand").setFirstVariation(true));
        registerBlock(new BlockOreDirt("oredirt"),
                block -> new ItemBlockMetadata(block, "iron_dirt").setFirstVariation(true));
        registerBlock(new BlockOreClay("oreclay"),
                block -> new ItemBlockMetadata(block, "tin_clay").setFirstVariation(true));
        registerBlock(new BlockOreStone("orestone"),
                block -> new ItemBlockMetadata(block, "gold_rock").setFirstVariation(true));
        registerBlock(new BlockEnergizedTallGrass("energizedtallgrass"),
                block -> new ItemBlockMetadata(block, "dead_bush", "tall_grass", "fern").setFirstVariation(true));

        registerBlock(BlockMetal.build("blockmetal").type(MaterialShape.BLOCK).create(),
                block -> new ItemBlockMetadata(block, block.variants.getAllowedValues().toArray(new String[0]))
                        .setFirstVariation(true));
        registerBlock(BlockMetal.build("blockmetalplate").type(MaterialShape.BLOCK_PLATE).create(),
                block -> new ItemBlockMetadata(block, block.variants.getAllowedValues().toArray(new String[0]))
                        .setFirstVariation(true));
        registerBlock(BlockScaffold.build("blockscaffold").type(MaterialShape.SCAFFOLD).create(),
                block -> new ItemBlockMetadata(block, block.variants.getAllowedValues().toArray(new String[0]))
                        .setFirstVariation(true));
    }
}
