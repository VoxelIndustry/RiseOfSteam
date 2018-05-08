package net.ros.common.init;

import net.ros.common.block.*;
import net.ros.common.block.item.ItemBlockMetadata;
import net.ros.common.tile.*;
import net.ros.common.tile.machine.TileBelt;
import net.ros.common.tile.TileFluidPipe;
import net.ros.common.tile.TileFluidPump;
import net.ros.common.tile.machine.TileExtractor;
import net.ros.common.tile.machine.TileSplitter;

public class LogisticBlocks
{
    public static void init()
    {
        ROSBlocks.registerBlock(new BlockPipeBase<>("fluidpipe", 6 / 16D,
                () -> new TileFluidPipe(64), TileFluidPipe.class));
        ROSBlocks.registerBlock(new BlockPipeBase<>("steampipe", 5 / 16D,
                () -> new TileSteamPipe(64, 1.5f), TileSteamPipe.class));
        ROSBlocks.registerBlock(new BlockFluidPump());
        ROSBlocks.registerBlock(new BlockOffshorePump());

        ROSBlocks.registerBlock(new BlockSteamValve());
        ROSBlocks.registerBlock(new BlockFluidValve());

        ROSBlocks.registerBlock(new BlockBelt());
        ROSBlocks.registerBlock(new BlockExtractor(), block -> new ItemBlockMetadata(block, "filter"));
        ROSBlocks.registerBlock(new BlockSplitter(), block -> new ItemBlockMetadata(block, "filter"));

        ROSBlocks.registerTile(TileFluidPipe.class, "fluidpipe");
        ROSBlocks.registerTile(TileSteamPipe.class, "steampipe");
        ROSBlocks.registerTile(TileFluidPump.class, "fluidpump");
        ROSBlocks.registerTile(TileOffshorePump.class, "offshore_pump");
        ROSBlocks.registerTile(TileBelt.class, "belt");
        ROSBlocks.registerTile(TileExtractor.class, "itemextractor");
        ROSBlocks.registerTile(TileSplitter.class, "itemsplitter");
        ROSBlocks.registerTile(TileSteamValve.class, "steamvalve");
        ROSBlocks.registerTile(TileFluidValve.class, "fluidvalve");
    }
}
