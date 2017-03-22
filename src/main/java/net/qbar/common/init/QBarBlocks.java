package net.qbar.common.init;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.qbar.QBar;
import net.qbar.common.block.*;
import net.qbar.common.block.creative.BlockCreativeSteamGenerator;
import net.qbar.common.block.item.ItemBlockMetadata;
import net.qbar.common.multiblock.BlockStructure;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.multiblock.TileMultiblockGag;
import net.qbar.common.tile.TileFluidPipe;
import net.qbar.common.tile.TileSolarBoiler;
import net.qbar.common.tile.TileSolarMirror;
import net.qbar.common.tile.TileSteamPipe;
import net.qbar.common.tile.TileStructure;
import net.qbar.common.tile.creative.TileCreativeSteamGenerator;
import net.qbar.common.tile.machine.TileAssembler;
import net.qbar.common.tile.machine.TileBelt;
import net.qbar.common.tile.machine.TileBoiler;
import net.qbar.common.tile.machine.TileExtractor;
import net.qbar.common.tile.machine.TileFluidPump;
import net.qbar.common.tile.machine.TileKeypunch;
import net.qbar.common.tile.machine.TileOffshorePump;
import net.qbar.common.tile.machine.TileSplitter;
import net.qbar.common.tile.machine.TileSteamFurnace;
import net.qbar.common.tile.machine.TileTank;

@ObjectHolder(QBar.MODID)
public class QBarBlocks
{
    @ObjectHolder("keypunch")
    public static final BlockMachineBase PUNCHING_MACHINE = null;
    @ObjectHolder("fluidtank")
    public static final BlockMachineBase FLUID_TANK       = null;
    @ObjectHolder("boiler")
    public static final BlockMachineBase SOLID_BOILER     = null;
    @ObjectHolder("fluidpipe")
    public static final BlockMachineBase FLUID_PIPE       = null;
    @ObjectHolder("steampipe")
    public static final BlockMachineBase STEAM_PIPE       = null;
    @ObjectHolder("fluidpump")
    public static final BlockMachineBase FLUID_PUMP       = null;
    @ObjectHolder("offshorepump")
    public static final BlockMachineBase OFFSHORE_PUMP    = null;
    @ObjectHolder("assembler")
    public static final BlockMachineBase ASSEMBLER        = null;

    // Creative
    @ObjectHolder("creative_steam_generator")
    public static final BlockMachineBase CREATIVE_BOILER  = null;

    @ObjectHolder("belt")
    public static final BlockMachineBase BELT             = null;
    @ObjectHolder("itemextractor")
    public static final BlockMachineBase ITEM_EXTRACTOR   = null;
    @ObjectHolder("itemsplitter")
    public static final BlockMachineBase ITEM_SPLITTER    = null;

    @ObjectHolder("structure")
    public static final BlockMachineBase STRUCTURE        = null;
    @ObjectHolder("steamfurnace")
    public static final BlockMachineBase STEAM_FURNACE    = null;

    public static final void registerBlocks()
    {
        QBarBlocks.registerBlock(new BlockKeypunch());
        QBarBlocks.registerBlock(
                new BlockTank("fluidtank_small", Multiblocks.SMALL_FLUID_TANK, Fluid.BUCKET_VOLUME * 48));
        QBarBlocks.registerBlock(
                new BlockTank("fluidtank_medium", Multiblocks.MEDIUM_FLUID_TANK, Fluid.BUCKET_VOLUME * 128));
        QBarBlocks.registerBlock(new BlockTank("fluidtank_big", Multiblocks.BIG_FLUID_TANK, Fluid.BUCKET_VOLUME * 432));
        QBarBlocks.registerBlock(new BlockBoiler());
        QBarBlocks.registerBlock(new BlockFluidPipe(), block -> new ItemBlockMetadata(block, "valve"));
        QBarBlocks.registerBlock(new BlockSteamPipe(), block -> new ItemBlockMetadata(block, "valve"));
        QBarBlocks.registerBlock(new BlockFluidPump());
        QBarBlocks.registerBlock(new BlockOffshorePump());
        QBarBlocks.registerBlock(new BlockAssembler());
        QBarBlocks.registerBlock(new BlockBelt());
        QBarBlocks.registerBlock(new BlockExtractor(), block -> new ItemBlockMetadata(block, "filter"));
        QBarBlocks.registerBlock(new BlockSplitter(), block -> new ItemBlockMetadata(block, "filter"));

        QBarBlocks.registerBlock(new BlockCreativeSteamGenerator());

        QBarBlocks.registerBlock(new BlockStructure());
        QBarBlocks.registerBlock(new BlockSteamFurnace());
        QBarBlocks.registerBlock(new BlockSolarBoiler());
        QBarBlocks.registerBlock(new BlockSolarMirror());

        QBarBlocks.registerTile(TileTank.class, "tank");
        QBarBlocks.registerTile(TileKeypunch.class, "keypunch");
        QBarBlocks.registerTile(TileBoiler.class, "boiler");
        QBarBlocks.registerTile(TileFluidPipe.class, "fluidpipe");
        QBarBlocks.registerTile(TileSteamPipe.class, "steampipe");
        QBarBlocks.registerTile(TileFluidPump.class, "fluidpump");
        QBarBlocks.registerTile(TileOffshorePump.class, "offshore_pump");
        QBarBlocks.registerTile(TileAssembler.class, "assembler");
        QBarBlocks.registerTile(TileBelt.class, "belt");
        QBarBlocks.registerTile(TileExtractor.class, "itemextractor");
        QBarBlocks.registerTile(TileSplitter.class, "itemsplitter");
        QBarBlocks.registerTile(TileMultiblockGag.class, "multiblockgag");
        QBarBlocks.registerTile(TileCreativeSteamGenerator.class, "creative_steam_generator");
        QBarBlocks.registerTile(TileStructure.class, "structure");
        QBarBlocks.registerTile(TileSteamFurnace.class, "steamfurnace");
        QBarBlocks.registerTile(TileSolarBoiler.class, "solarboiler");
        QBarBlocks.registerTile(TileSolarMirror.class, "solarmirror");
    }

    public static final void registerBlock(final Block block, final String name)
    {
        QBarBlocks.registerBlock(block, ItemBlock::new, name);
    }

    public static final void registerBlock(final Block block, final Function<Block, ItemBlock> supplier,
            final String name)
    {
        final ItemBlock supplied = supplier.apply(block);
        GameRegistry.register(block.setRegistryName(QBar.MODID, name));
        GameRegistry.register(supplied, block.getRegistryName());

        QBar.proxy.registerItemRenderer(supplied, 0, name);
    }

    public static final void registerBlock(final BlockBase block)
    {
        QBarBlocks.registerBlock(block, block.name);
    }

    public static final void registerBlock(final BlockMachineBase block)
    {
        QBarBlocks.registerBlock(block, block.name);
    }

    public static final void registerBlock(final BlockMachineBase block, final Function<Block, ItemBlock> supplier)
    {
        QBarBlocks.registerBlock(block, supplier, block.name);
    }

    public static final void registerTile(final Class<? extends TileEntity> c, final String name)
    {
        GameRegistry.registerTileEntity(c, QBar.MODID + ":" + name);
    }
}