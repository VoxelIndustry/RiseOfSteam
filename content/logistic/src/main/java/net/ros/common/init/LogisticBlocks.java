package net.ros.common.init;

import net.ros.common.block.*;
import net.ros.common.block.item.ItemBlockMetadata;
import net.ros.common.tile.*;
import net.ros.common.tile.machine.TileBelt;
import net.ros.common.tile.machine.TileExtractor;
import net.ros.common.tile.machine.TileSplitter;

import static net.ros.common.init.ROSBlocks.registerBlock;
import static net.ros.common.init.ROSBlocks.registerTile;

public class LogisticBlocks
{
    public static void init()
    {
        registerBlock(new BlockPipeBase<>("fluidpipe", 6 / 16D,
                () -> new TileFluidPipe(64), TileFluidPipe.class));
        registerBlock(new BlockPipeBase<>("steampipe", 5 / 16D,
                () -> new TileSteamPipe(64, 1.5f), TileSteamPipe.class));
        registerBlock(new BlockFluidPump());
        registerBlock(new BlockOffshorePump());

        registerBlock(new BlockPipeValve<>("fluidvalve", 6 / 16D,
                () -> new TileFluidValve(64), TileFluidValve.class));
        registerBlock(new BlockPipeValve<>("steamvalve", 5 / 16D,
                () -> new TileSteamValve(64, 1.5f), TileSteamValve.class));

        registerBlock(new BlockSteamGauge("steamgauge", 5 / 16D,
                () -> new TileSteamGauge(64, 1.5f)));

        registerBlock(new BlockBelt());
        registerBlock(new BlockExtractor(), block -> new ItemBlockMetadata(block, "filter"));
        registerBlock(new BlockSplitter(), block -> new ItemBlockMetadata(block, "filter"));

        registerBlock(new BlockSteamVent());

        registerTile(TileFluidPipe.class, "fluidpipe");
        registerTile(TileSteamPipe.class, "steampipe");
        registerTile(TileFluidPump.class, "fluidpump");
        registerTile(TileOffshorePump.class, "offshore_pump");
        registerTile(TileBelt.class, "belt");
        registerTile(TileExtractor.class, "itemextractor");
        registerTile(TileSplitter.class, "itemsplitter");
        registerTile(TileSteamValve.class, "steamvalve");
        registerTile(TileFluidValve.class, "fluidvalve");
        registerTile(TileSteamVent.class, "steamvent");
        registerTile(TileSteamGauge.class, "steamgauge");
    }
}
