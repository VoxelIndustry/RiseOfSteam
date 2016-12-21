package net.qbar.common.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.QBar;
import net.qbar.common.block.BlockPunchingMachine;

/**
 * @author Ourten 21 déc. 2016
 */
public class QBarBlocks
{
    private static final Block punchingMachine = new BlockPunchingMachine();

    public static final void registerBlocks()
    {
        QBarBlocks.registerBlock(QBarBlocks.punchingMachine, "BlockPunchingMachine");
    }

    public static final void registerBlock(final Block block, final String name)
    {
        block.setRegistryName(QBar.MODID, name);
        GameRegistry.register(block);
    }
}