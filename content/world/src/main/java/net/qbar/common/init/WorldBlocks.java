package net.qbar.common.init;

import net.qbar.common.block.*;
import net.qbar.common.block.item.ItemBlockMetadata;
import net.qbar.common.block.item.ItemBlockVeinOre;
import net.qbar.common.ore.QBarOres;
import net.qbar.common.recipe.QBarMaterials;

public class WorldBlocks
{
    public static void init()
    {
        QBarBlocks.registerBlock(new BlockVeinOre.Builder("ironnickelore").addContent(QBarOres.PENTLANDITE)
                .addContent(QBarOres.GARNIERITE).addContent(QBarOres.LATERITE).create(), ItemBlockVeinOre::new);
        QBarBlocks.registerBlock(
                new BlockVeinOre.Builder("ironcopperore").addContent(QBarOres.CHALCOPYRITE)
                        .addContent(QBarOres.TETRAHEDRITE).addContent(QBarOres.MALACHITE).create(),
                ItemBlockVeinOre::new);
        QBarBlocks.registerBlock(new BlockVeinOre.Builder("tinore").addContent(QBarOres.CASSITERITE)
                .addContent(QBarOres.TEALLITE).create(), ItemBlockVeinOre::new);
        QBarBlocks.registerBlock(new BlockVeinOre.Builder("ironzincore").addContent(QBarOres.SPHALERITE).create(),
                ItemBlockVeinOre::new);
        QBarBlocks.registerBlock(new BlockVeinOre.Builder("goldore").addContent(QBarOres.GOLD_ORE).create(),
                ItemBlockVeinOre::new);
        QBarBlocks.registerBlock(new BlockVeinOre.Builder("redstoneore").addContent(QBarOres.REDSTONE_ORE).create(),
                ItemBlockVeinOre::new);

        QBarBlocks.registerBlock(new BlockOreSand("oresand"),
                block -> new ItemBlockMetadata(block, "copper_sand", "tin_sand").setFirstVariation(true));
        QBarBlocks.registerBlock(new BlockOreDirt("oredirt"),
                block -> new ItemBlockMetadata(block, "iron_dirt").setFirstVariation(true));
        QBarBlocks.registerBlock(new BlockOreClay("oreclay"),
                block -> new ItemBlockMetadata(block, "tin_clay").setFirstVariation(true));
        QBarBlocks.registerBlock(new BlockOreStone("orestone"),
                block -> new ItemBlockMetadata(block, "gold_rock").setFirstVariation(true));
        QBarBlocks.registerBlock(new BlockEnergizedTallGrass("energizedtallgrass"),
                block -> new ItemBlockMetadata(block, "dead_bush", "tall_grass", "fern").setFirstVariation(true));

        QBarBlocks.registerBlock(new BlockMetal(), block -> new ItemBlockMetadata(block,
                QBarMaterials.metals.toArray(new String[BlockMetal.VARIANTS.getAllowedValues().size()]))
                .setFirstVariation(true));
    }
}
