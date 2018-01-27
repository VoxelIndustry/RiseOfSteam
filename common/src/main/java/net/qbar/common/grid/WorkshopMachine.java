package net.qbar.common.grid;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.qbar.common.init.QBarBlocks;

public enum WorkshopMachine
{
    KEYPUNCH, WORKBENCH, CARDLIBRARY, PRINTER, STORAGE;

    public static WorkshopMachine[] VALUES = new WorkshopMachine[]{KEYPUNCH, WORKBENCH, CARDLIBRARY, PRINTER, STORAGE};

    public Block getBlock()
    {
        switch (this)
        {
            case KEYPUNCH:
                return QBarBlocks.PUNCHING_MACHINE;
            case WORKBENCH:
                return QBarBlocks.ENGINEER_WORKBENCH;
            case CARDLIBRARY:
                return QBarBlocks.CRAFT_CARD_LIBRARY;
            case PRINTER:
                return QBarBlocks.BLUEPRINT_PRINTER;
            case STORAGE:
                return QBarBlocks.ENGINEER_STORAGE;
            default:
                return Blocks.DIRT;
        }
    }
}
