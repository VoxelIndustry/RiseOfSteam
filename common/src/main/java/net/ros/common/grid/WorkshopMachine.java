package net.ros.common.grid;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.ros.common.init.ROSBlocks;

public enum WorkshopMachine
{
    KEYPUNCH, WORKBENCH, CARDLIBRARY, PRINTER, STORAGE;

    public static WorkshopMachine[] VALUES = new WorkshopMachine[]{KEYPUNCH, WORKBENCH, CARDLIBRARY, PRINTER, STORAGE};

    public Block getBlock()
    {
        switch (this)
        {
            case KEYPUNCH:
                return ROSBlocks.PUNCHING_MACHINE;
            case WORKBENCH:
                return ROSBlocks.ENGINEER_WORKBENCH;
            case CARDLIBRARY:
                return ROSBlocks.CRAFT_CARD_LIBRARY;
            case PRINTER:
                return ROSBlocks.BLUEPRINT_PRINTER;
            case STORAGE:
                return ROSBlocks.ENGINEER_STORAGE;
            default:
                return Blocks.DIRT;
        }
    }
}
