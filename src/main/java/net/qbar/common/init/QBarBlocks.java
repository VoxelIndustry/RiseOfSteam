package net.qbar.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.QBar;
import net.qbar.common.block.BlockAssembler;
import net.qbar.common.block.BlockBase;
import net.qbar.common.block.BlockBelt;
import net.qbar.common.block.BlockBoiler;
import net.qbar.common.block.BlockFluidPipe;
import net.qbar.common.block.BlockFluidPump;
import net.qbar.common.block.BlockKeypunch;
import net.qbar.common.block.BlockMachineBase;
import net.qbar.common.block.BlockOffshorePump;
import net.qbar.common.block.BlockSteamPipe;
import net.qbar.common.block.BlockTank;
import net.qbar.common.tile.TileAssembler;
import net.qbar.common.tile.TileBelt;
import net.qbar.common.tile.TileBoiler;
import net.qbar.common.tile.TileFluidPipe;
import net.qbar.common.tile.TileFluidPump;
import net.qbar.common.tile.TileKeypunch;
import net.qbar.common.tile.TileOffshorePump;
import net.qbar.common.tile.TileSteamPipe;
import net.qbar.common.tile.TileTank;

public class QBarBlocks
{
    private static final BlockMachineBase PUNCHING_MACHINE = new BlockKeypunch();
    private static final BlockMachineBase FLUID_TANK       = new BlockTank();
    private static final BlockMachineBase SOLID_BOILER     = new BlockBoiler();
    private static final BlockMachineBase FLUID_PIPE       = new BlockFluidPipe();
    private static final BlockMachineBase STEAM_PIPE       = new BlockSteamPipe();
    private static final BlockMachineBase FLUID_PUMP       = new BlockFluidPump();
    private static final BlockMachineBase OFFSHORE_PUMP    = new BlockOffshorePump();
    private static final BlockMachineBase ASSEMBLER        = new BlockAssembler();

    public static final BlockMachineBase  BELT             = new BlockBelt();

    public static final void registerBlocks()
    {
        QBarBlocks.registerBlock(QBarBlocks.PUNCHING_MACHINE);
        QBarBlocks.registerBlock(QBarBlocks.FLUID_TANK);
        QBarBlocks.registerBlock(QBarBlocks.SOLID_BOILER);
        QBarBlocks.registerBlock(QBarBlocks.FLUID_PIPE);
        QBarBlocks.registerBlock(QBarBlocks.STEAM_PIPE);
        QBarBlocks.registerBlock(QBarBlocks.FLUID_PUMP);
        QBarBlocks.registerBlock(QBarBlocks.OFFSHORE_PUMP);
        QBarBlocks.registerBlock(QBarBlocks.ASSEMBLER);
        QBarBlocks.registerBlock(QBarBlocks.BELT);

        QBarBlocks.registerTile(TileTank.class, "tank");
        QBarBlocks.registerTile(TileKeypunch.class, "keypunch");
        QBarBlocks.registerTile(TileBoiler.class, "boiler");
        QBarBlocks.registerTile(TileFluidPipe.class, "fluidpipe");
        QBarBlocks.registerTile(TileSteamPipe.class, "steampipe");
        QBarBlocks.registerTile(TileFluidPump.class, "fluidpump");
        QBarBlocks.registerTile(TileOffshorePump.class, "offshore_pump");
        QBarBlocks.registerTile(TileAssembler.class, "assembler");
        QBarBlocks.registerTile(TileBelt.class, "belt");
    }

    public static final void registerBlock(final Block block, final String name)
    {
        final ItemBlock itemBlock = new ItemBlock(block);

        GameRegistry.register(block.setRegistryName(QBar.MODID, name));
        GameRegistry.register(itemBlock, block.getRegistryName());

        QBar.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    public static final void registerBlock(final BlockBase block)
    {
        QBarBlocks.registerBlock(block, block.name);
    }

    public static final void registerBlock(final BlockMachineBase block)
    {
        QBarBlocks.registerBlock(block, block.name);
    }

    public static final void registerTile(final Class<? extends TileEntity> c, final String name)
    {
        GameRegistry.registerTileEntity(c, QBar.MODID + ":" + name);
    }
}