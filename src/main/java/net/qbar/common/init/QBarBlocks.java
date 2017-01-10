package net.qbar.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.QBar;
import net.qbar.common.block.BlockBase;
import net.qbar.common.block.BlockBoiler;
import net.qbar.common.block.BlockFluidPipe;
import net.qbar.common.block.BlockFluidPump;
import net.qbar.common.block.BlockKeypunch;
import net.qbar.common.block.BlockMachineBase;
import net.qbar.common.block.BlockSteamPipe;
import net.qbar.common.block.BlockTank;
import net.qbar.common.tile.TileBoiler;
import net.qbar.common.tile.TileFluidPipe;
import net.qbar.common.tile.TileFluidPump;
import net.qbar.common.tile.TileKeypunch;
import net.qbar.common.tile.TileSteamPipe;
import net.qbar.common.tile.TileTank;

public class QBarBlocks
{
    private static final BlockMachineBase punchingMachine = new BlockKeypunch();
    private static final BlockMachineBase tank            = new BlockTank();
    private static final BlockMachineBase boiler          = new BlockBoiler();
    private static final BlockMachineBase fluidPipe       = new BlockFluidPipe();
    private static final BlockMachineBase steamPipe       = new BlockSteamPipe();
    private static final BlockMachineBase fluidPump       = new BlockFluidPump();

    public static final void registerBlocks()
    {
        QBarBlocks.registerBlock(QBarBlocks.punchingMachine);
        QBarBlocks.registerBlock(QBarBlocks.tank);
        QBarBlocks.registerBlock(QBarBlocks.boiler);
        QBarBlocks.registerBlock(QBarBlocks.fluidPipe);
        QBarBlocks.registerBlock(QBarBlocks.steamPipe);
        QBarBlocks.registerBlock(QBarBlocks.fluidPump);

        QBarBlocks.registerTile(TileTank.class, "tank");
        QBarBlocks.registerTile(TileKeypunch.class, "keypunch");
        QBarBlocks.registerTile(TileBoiler.class, "boiler");
        QBarBlocks.registerTile(TileFluidPipe.class, "fluidpipe");
        QBarBlocks.registerTile(TileSteamPipe.class, "steampipe");
        QBarBlocks.registerTile(TileFluidPump.class, "fluidpump");
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