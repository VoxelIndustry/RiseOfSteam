package net.ros.common.init;

import net.ros.common.block.*;
import net.ros.common.block.item.ItemBlockMetadata;
import net.ros.common.block.item.ItemBlockVeinOre;
import net.ros.common.ore.Ores;
import net.ros.common.recipe.Materials;

public class WorldBlocks
{
    public static void init()
    {
        ROSBlocks.registerBlock(new BlockVeinOre.Builder("ironnickelore").addContent(Ores.PENTLANDITE)
                .addContent(Ores.GARNIERITE).addContent(Ores.LATERITE).create(), ItemBlockVeinOre::new);
        ROSBlocks.registerBlock(
                new BlockVeinOre.Builder("ironcopperore").addContent(Ores.CHALCOPYRITE)
                        .addContent(Ores.TETRAHEDRITE).addContent(Ores.MALACHITE).create(),
                ItemBlockVeinOre::new);
        ROSBlocks.registerBlock(new BlockVeinOre.Builder("tinore").addContent(Ores.CASSITERITE)
                .addContent(Ores.TEALLITE).create(), ItemBlockVeinOre::new);
        ROSBlocks.registerBlock(new BlockVeinOre.Builder("ironzincore").addContent(Ores.SPHALERITE).create(),
                ItemBlockVeinOre::new);
        ROSBlocks.registerBlock(new BlockVeinOre.Builder("goldore").addContent(Ores.GOLD_ORE).create(),
                ItemBlockVeinOre::new);
        ROSBlocks.registerBlock(new BlockVeinOre.Builder("redstoneore").addContent(Ores.REDSTONE_ORE).create(),
                ItemBlockVeinOre::new);

        ROSBlocks.registerBlock(new BlockOreSand("oresand"),
                block -> new ItemBlockMetadata(block, "copper_sand", "tin_sand").setFirstVariation(true));
        ROSBlocks.registerBlock(new BlockOreDirt("oredirt"),
                block -> new ItemBlockMetadata(block, "iron_dirt").setFirstVariation(true));
        ROSBlocks.registerBlock(new BlockOreClay("oreclay"),
                block -> new ItemBlockMetadata(block, "tin_clay").setFirstVariation(true));
        ROSBlocks.registerBlock(new BlockOreStone("orestone"),
                block -> new ItemBlockMetadata(block, "gold_rock").setFirstVariation(true));
        ROSBlocks.registerBlock(new BlockEnergizedTallGrass("energizedtallgrass"),
                block -> new ItemBlockMetadata(block, "dead_bush", "tall_grass", "fern").setFirstVariation(true));

        ROSBlocks.registerBlock(new BlockMetal(), block -> new ItemBlockMetadata(block,
                Materials.metals.toArray(new String[BlockMetal.VARIANTS.getAllowedValues().size()]))
                .setFirstVariation(true));
    }
}
