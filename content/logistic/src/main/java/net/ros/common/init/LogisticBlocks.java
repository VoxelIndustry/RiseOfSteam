package net.ros.common.init;

import fr.ourten.teabeans.function.PetaFunction;
import net.ros.common.block.*;
import net.ros.common.block.item.ItemBlockMetadata;
import net.ros.common.grid.node.PipeNature;
import net.ros.common.grid.node.PipeSize;
import net.ros.common.grid.node.PipeType;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.Metal;
import net.ros.common.tile.*;
import net.ros.common.tile.machine.TileBelt;
import net.ros.common.tile.machine.TileExtractor;
import net.ros.common.tile.machine.TileSplitter;

import java.util.function.Function;

import static net.ros.common.init.ROSBlocks.registerBlock;
import static net.ros.common.init.ROSBlocks.registerTile;

public class LogisticBlocks
{
    private static <B extends BlockPipeBase<T>, T extends TilePipeBase> void addPipe(
            PetaFunction<String, Double, PipeType, Function<PipeType, T>, Class<T>, B> blockSupplier,
            PipeNature nature, Metal material, float radiusOffset, Function<PipeType, T> tileSupplier, Class<T>
                    tileClass)
    {
        for (PipeSize size: PipeSize.values())
        {
            if(size.ordinal() > PipeSize.LARGE.ordinal())
                continue;
            registerBlock(blockSupplier.apply(nature.toString() + "pipe_" + material.getName() + "_" + size.toString(),
                    (double) (size.getRadius() + radiusOffset), new PipeType(nature, size, material),
                    tileSupplier, tileClass));
        }
    }

    public static void init()
    {
        addPipe(BlockPipeBase::new, PipeNature.FLUID, Materials.IRON, 0,
                TileFluidPipe::new, TileFluidPipe.class);
        addPipe(BlockPipeBase::new, PipeNature.FLUID, Materials.CAST_IRON, 0,
                TileFluidPipe::new, TileFluidPipe.class);

        addPipe(BlockPipeValve::new, PipeNature.FLUID, Materials.IRON, 0,
                TileFluidValve::new, TileFluidValve.class);
        addPipe(BlockPipeValve::new, PipeNature.FLUID, Materials.CAST_IRON, 0,
                TileFluidValve::new, TileFluidValve.class);

        addPipe(BlockPipeBase::new, PipeNature.STEAM, Materials.BRASS, -1 / 16F,
                TileSteamPipe::new, TileSteamPipe.class);
        addPipe(BlockPipeBase::new, PipeNature.STEAM, Materials.STEEL, -1 / 16F,
                TileSteamPipe::new, TileSteamPipe.class);

        addPipe(BlockPipeValve::new, PipeNature.STEAM, Materials.BRASS, -1 / 16F,
                TileSteamValve::new, TileSteamValve.class);
        addPipe(BlockPipeValve::new, PipeNature.STEAM, Materials.STEEL, -1 / 16F,
                TileSteamValve::new, TileSteamValve.class);

        addPipe(BlockSteamGauge::new, PipeNature.STEAM, Materials.BRASS, -1 / 16F,
                TileSteamGauge::new, TileSteamGauge.class);
        addPipe(BlockSteamGauge::new, PipeNature.STEAM, Materials.STEEL, -1 / 16F,
                TileSteamGauge::new, TileSteamGauge.class);

        addPipe(BlockPressureValve::new, PipeNature.STEAM, Materials.BRASS, -1 / 16F,
                TilePressureValve::new, TilePressureValve.class);
        addPipe(BlockPressureValve::new, PipeNature.STEAM, Materials.STEEL, -1 / 16F,
                TilePressureValve::new, TilePressureValve.class);

        addPipe(BlockSteamVent::new, PipeNature.STEAM, Materials.BRASS, -1 / 16F,
                TileSteamVent::new, TileSteamVent.class);
        addPipe(BlockSteamVent::new, PipeNature.STEAM, Materials.STEEL, -1 / 16F,
                TileSteamVent::new, TileSteamVent.class);

        registerBlock(new BlockFluidPump());
        registerBlock(new BlockOffshorePump());

        registerBlock(new BlockBelt());
        registerBlock(new BlockExtractor(), block -> new ItemBlockMetadata(block, "filter"));
        registerBlock(new BlockSplitter(), block -> new ItemBlockMetadata(block, "filter"));

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
