package net.ros.common.init;

import fr.ourten.teabeans.function.PetaFunction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.ROSConstants;
import net.ros.common.block.*;
import net.ros.common.block.item.ItemBlockMetadata;
import net.ros.common.grid.node.IPipeValve;
import net.ros.common.grid.node.PipeNature;
import net.ros.common.grid.node.PipeSize;
import net.ros.common.grid.node.PipeType;
import net.ros.common.gui.LogisticGui;
import net.ros.common.multiblock.BlockMultiblockBase;
import net.ros.common.multiblock.RightClickAction;
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
        for (PipeSize size : PipeSize.values())
        {
            if (size.ordinal() > PipeSize.LARGE.ordinal())
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

        addPipe(BlockPipeCover.getSupplier(PipeCoverType.VALVE, LogisticBlocks::onValveRightClick,
                new AxisAlignedBB(3 / 16D, 3 / 16D, 0, 13 / 16D, 13 / 16D, 7 / 16D)),
                PipeNature.FLUID, Materials.IRON, 0, TileFluidValve::new, TileFluidValve.class);
        addPipe(BlockPipeCover.getSupplier(PipeCoverType.VALVE, LogisticBlocks::onValveRightClick,
                new AxisAlignedBB(3 / 16D, 3 / 16D, 0, 13 / 16D, 13 / 16D, 7 / 16D)),
                PipeNature.FLUID, Materials.CAST_IRON, 0, TileFluidValve::new, TileFluidValve.class);

        addPipe(BlockPipeBase::new, PipeNature.STEAM, Materials.BRASS, -1 / 16F,
                TileSteamPipe::new, TileSteamPipe.class);
        addPipe(BlockPipeBase::new, PipeNature.STEAM, Materials.STEEL, -1 / 16F,
                TileSteamPipe::new, TileSteamPipe.class);

        addPipe(BlockPipeCover.getSupplier(PipeCoverType.VALVE, LogisticBlocks::onValveRightClick,
                new AxisAlignedBB(3 / 16D, 3 / 16D, 0, 13 / 16D, 13 / 16D, 7 / 16D)),
                PipeNature.STEAM, Materials.BRASS, -1 / 16F, TileSteamValve::new, TileSteamValve.class);
        addPipe(BlockPipeCover.getSupplier(PipeCoverType.VALVE, LogisticBlocks::onValveRightClick,
                new AxisAlignedBB(3 / 16D, 3 / 16D, 0, 13 / 16D, 13 / 16D, 7 / 16D)),
                PipeNature.STEAM, Materials.STEEL, -1 / 16F, TileSteamValve::new, TileSteamValve.class);

        addPipe(BlockPipeCover.getSupplier(PipeCoverType.STEAM_GAUGE, RightClickAction.EMPTY,
                new AxisAlignedBB(6 / 16D, 6 / 16D, 4 / 16D, 10 / 16D, 10 / 16D, 7 / 16D)),
                PipeNature.STEAM, Materials.BRASS, -1 / 16F, TileSteamGauge::new, TileSteamGauge.class);
        addPipe(BlockPipeCover.getSupplier(PipeCoverType.STEAM_GAUGE, RightClickAction.EMPTY,
                new AxisAlignedBB(6 / 16D, 6 / 16D, 4 / 16D, 10 / 16D, 10 / 16D, 7 / 16D)),
                PipeNature.STEAM, Materials.STEEL, -1 / 16F, TileSteamGauge::new, TileSteamGauge.class);

        addPipe(BlockPressureValve::new, PipeNature.STEAM, Materials.BRASS, -1 / 16F,
                TilePressureValve::new, TilePressureValve.class);
        addPipe(BlockPressureValve::new, PipeNature.STEAM, Materials.STEEL, -1 / 16F,
                TilePressureValve::new, TilePressureValve.class);

        addPipe(BlockPipeCover.getSupplier(PipeCoverType.STEAM_VENT, LogisticBlocks::onVentRightClick,
                new AxisAlignedBB(1 / 16D, 2 / 16D, 0, 11 / 16D, 1, 7 / 16D)),
                PipeNature.STEAM, Materials.BRASS, -1 / 16F, TileSteamVent::new, TileSteamVent.class);
        addPipe(BlockPipeCover.getSupplier(PipeCoverType.STEAM_VENT, LogisticBlocks::onVentRightClick,
                new AxisAlignedBB(1 / 16D, 2 / 16D, 0, 11 / 16D, 1, 7 / 16D)),
                PipeNature.STEAM, Materials.STEEL, -1 / 16F, TileSteamVent::new, TileSteamVent.class);

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

    private static boolean onValveRightClick(World w, BlockPos pos, IBlockState state, EntityPlayer player,
                                             EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!w.isRemote)
        {
            IPipeValve valve;
            if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
                valve = (IPipeValve) w.getTileEntity(pos.offset(state.getValue(BlockOrientableMachine.FACING)));
            else
                valve = (IPipeValve) w.getTileEntity(pos);
            if (valve == null)
                return false;
            valve.setOpen(!valve.isOpen());
        }
        return true;
    }

    private static boolean onVentRightClick(World w, BlockPos pos, IBlockState state, EntityPlayer player,
                                            EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (w.isRemote)
            return true;
        BlockPos offset = pos;

        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            offset = pos.offset(state.getValue(BlockOrientableMachine.FACING).getOpposite());

        player.openGui(ROSConstants.MODINSTANCE, LogisticGui.STEAM_VENT.getUniqueID(), w, offset.getX(),
                offset.getY(), offset.getZ());
        return true;
    }
}
