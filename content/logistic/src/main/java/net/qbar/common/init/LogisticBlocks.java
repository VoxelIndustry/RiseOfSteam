package net.qbar.common.init;

import net.qbar.common.block.*;
import net.qbar.common.block.item.ItemBlockMetadata;
import net.qbar.common.tile.*;
import net.qbar.common.tile.machine.TileBelt;
import net.qbar.common.tile.machine.TileExtractor;
import net.qbar.common.tile.machine.TileSplitter;

public class LogisticBlocks
{
    public static void init()
    {
        QBarBlocks.registerBlock(new BlockFluidPipe());
        QBarBlocks.registerBlock(new BlockSteamPipe());
        QBarBlocks.registerBlock(new BlockFluidPump());
        QBarBlocks.registerBlock(new BlockOffshorePump());

        QBarBlocks.registerBlock(new BlockSteamValve());

        QBarBlocks.registerBlock(new BlockBelt());
        QBarBlocks.registerBlock(new BlockExtractor(), block -> new ItemBlockMetadata(block, "filter"));
        QBarBlocks.registerBlock(new BlockSplitter(), block -> new ItemBlockMetadata(block, "filter"));

        QBarBlocks.registerTile(TileFluidPipe.class, "fluidpipe");
        QBarBlocks.registerTile(TileSteamPipe.class, "steampipe");
        QBarBlocks.registerTile(TileFluidPump.class, "fluidpump");
        QBarBlocks.registerTile(TileOffshorePump.class, "offshore_pump");
        QBarBlocks.registerTile(TileBelt.class, "belt");
        QBarBlocks.registerTile(TileExtractor.class, "itemextractor");
        QBarBlocks.registerTile(TileSplitter.class, "itemsplitter");
        QBarBlocks.registerTile(TileSteamValve.class, "steamvalve");
    }
}
