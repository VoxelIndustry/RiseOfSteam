package net.qbar.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.QBar;
import net.qbar.common.block.BlockBase;
import net.qbar.common.block.BlockKeypunch;

/**
 * @author Ourten 21 déc. 2016
 */
public class QBarBlocks
{
    private static final BlockBase punchingMachine = new BlockKeypunch();

    public static final void registerBlocks()
    {
        QBarBlocks.registerBlock(QBarBlocks.punchingMachine);
    }

    public static final void registerBlock(final Block block, String name)
	{
        GameRegistry.register(block.setRegistryName(QBar.MODID, name));
        GameRegistry.register(new ItemBlock(block), block.getRegistryName());
	}

    public static final void registerBlock(final BlockBase block)
    {
    	registerBlock(block, block.name);
    }
}