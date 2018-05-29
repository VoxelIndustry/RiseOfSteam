package net.ros.common.init;

import net.ros.common.block.*;
import net.ros.common.block.item.ItemBlockMetadata;
import net.ros.common.grid.node.PipeNature;
import net.ros.common.grid.node.PipeSize;
import net.ros.common.grid.node.PipeType;
import net.ros.common.recipe.Materials;
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
        registerBlock(new BlockPipeBase<>("fluidpipe_small", 6 / 16D,
                new PipeType(PipeNature.FLUID, PipeSize.SMALL, Materials.IRON),
                (type) -> new TileFluidPipe(type, 64), TileFluidPipe.class));
        registerBlock(new BlockPipeBase<>("fluidpipe_medium", 11 / 16D,
                new PipeType(PipeNature.FLUID, PipeSize.MEDIUM, Materials.IRON),
                (type) -> new TileFluidPipe(type, 256), TileFluidPipe.class));
        registerBlock(new BlockPipeBase<>("fluidpipe_large", 1,
                new PipeType(PipeNature.FLUID, PipeSize.LARGE, Materials.IRON),
                (type) -> new TileFluidPipe(type, 1024), TileFluidPipe.class));

        registerBlock(new BlockPipeBase<>("steampipe_small", 5 / 16D,
                new PipeType(PipeNature.STEAM, PipeSize.SMALL, Materials.BRASS),
                (type) -> new TileSteamPipe(type, 64, 1.5f), TileSteamPipe.class));
        registerBlock(new BlockPipeBase<>("steampipe_medium", 10 / 16D,
                new PipeType(PipeNature.STEAM, PipeSize.MEDIUM, Materials.BRASS),
                (type) -> new TileSteamPipe(type, 256, 2f), TileSteamPipe.class));
        registerBlock(new BlockPipeBase<>("steampipe_large", 15 / 16D,
                new PipeType(PipeNature.STEAM, PipeSize.LARGE, Materials.BRASS),
                (type) -> new TileSteamPipe(type, 1024, 2.5f), TileSteamPipe.class));
        registerBlock(new BlockFluidPump());
        registerBlock(new BlockOffshorePump());

        registerBlock(new BlockPipeValve<>("fluidvalve_small", 6 / 16D,
                new PipeType(PipeNature.FLUID, PipeSize.SMALL, Materials.IRON),
                (type) -> new TileFluidValve(type, 64), TileFluidValve.class));
        registerBlock(new BlockPipeValve<>("fluidvalve_medium", 11 / 16D,
                new PipeType(PipeNature.FLUID, PipeSize.MEDIUM, Materials.IRON),
                (type) -> new TileFluidValve(type, 256), TileFluidValve.class));

        registerBlock(new BlockPipeValve<>("steamvalve_small", 5 / 16D,
                new PipeType(PipeNature.STEAM, PipeSize.SMALL, Materials.BRASS),
                (type) -> new TileSteamValve(type, 64, 1.5f), TileSteamValve.class));
        registerBlock(new BlockPipeValve<>("steamvalve_medium", 10 / 16D,
                new PipeType(PipeNature.STEAM, PipeSize.MEDIUM, Materials.BRASS),
                (type) -> new TileSteamValve(type, 256, 2f), TileSteamValve.class));

        registerBlock(new BlockSteamGauge("steamgauge_small", 5 / 16D,
                new PipeType(PipeNature.STEAM, PipeSize.SMALL, Materials.BRASS),
                (type) -> new TileSteamGauge(type, 64, 1.5f)));
        registerBlock(new BlockSteamGauge("steamgauge_medium", 10 / 16D,
                new PipeType(PipeNature.STEAM, PipeSize.MEDIUM, Materials.BRASS),
                (type) -> new TileSteamGauge(type, 256, 2f)));
        registerBlock(new BlockSteamGauge("steamgauge_large", 15 / 16D,
                new PipeType(PipeNature.STEAM, PipeSize.LARGE, Materials.BRASS),
                (type) -> new TileSteamGauge(type, 1024, 2.5f)));

        registerBlock(new BlockPressureValve("pressurevalve_small", 5 / 16D,
                new PipeType(PipeNature.STEAM, PipeSize.SMALL, Materials.BRASS),
                (type) -> new TilePressureValve(type, 64, 1.5f)));

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
        registerTile(TilePressureValve.class, "pressurevalve");
    }
}
