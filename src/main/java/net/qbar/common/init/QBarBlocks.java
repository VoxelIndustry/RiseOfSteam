package net.qbar.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.QBar;
import net.qbar.common.block.BlockBase;
import net.qbar.common.block.BlockKeypunch;
import net.qbar.common.block.BlockMachineBase;
import net.qbar.common.block.BlockTank;
import net.qbar.common.tile.TileKeypunch;
import net.qbar.common.tile.TileTank;

public class QBarBlocks
{
    private static final BlockMachineBase punchingMachine = new BlockKeypunch();
    private static final BlockMachineBase tank            = new BlockTank();

    public static final void registerBlocks()
    {
        QBarBlocks.registerBlock(QBarBlocks.punchingMachine);
        QBarBlocks.registerBlock(QBarBlocks.tank);

        QBarBlocks.registerTile(TileTank.class, "tank");
        QBarBlocks.registerTile(TileKeypunch.class, "keypunch");
    }

    public static final void registerBlock(final Block block, final String name)
    {
        GameRegistry.register(block.setRegistryName(QBar.MODID, name));
        GameRegistry.register(new ItemBlock(block), block.getRegistryName());
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
        GameRegistry.registerTileEntity(c, QBar.MODID + ":tank" + name);
    }
}