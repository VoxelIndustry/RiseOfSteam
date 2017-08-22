package net.qbar.common.multiblock;

public class Multiblocks
{
    public static final IMultiblockDescriptor SMALL_FLUID_TANK;
    public static final IMultiblockDescriptor KEYPUNCH;
    public static final IMultiblockDescriptor ASSEMBLER;
    public static final IMultiblockDescriptor SOLID_BOILER;

    public static final IMultiblockDescriptor MEDIUM_FLUID_TANK;
    public static final IMultiblockDescriptor BIG_FLUID_TANK;
    public static final IMultiblockDescriptor ROLLING_MILL;
    public static final IMultiblockDescriptor SOLAR_BOILER;
    public static final IMultiblockDescriptor LIQUID_FUEL_BOILER;
    public static final IMultiblockDescriptor STEAM_FURNACE_MK1;
    public static final IMultiblockDescriptor STEAM_FURNACE_MK2;
    public static final IMultiblockDescriptor SOLAR_MIRROR;

    public static final IMultiblockDescriptor SMALL_MINING_DRILL;
    public static final IMultiblockDescriptor TINY_MINING_DRILL;
    public static final IMultiblockDescriptor ORE_WASHER;
    public static final IMultiblockDescriptor SORTING_MACHINE;
    public static final IMultiblockDescriptor ALLOY_CAULDRON;
    public static final IMultiblockDescriptor SAW_MILL;

    public static final IMultiblockDescriptor ENGINEER_WORKBENCH;
    public static final IMultiblockDescriptor BLUEPRINT_PRINTER;
    public static final IMultiblockDescriptor CRAFT_CARD_LIBRARY;

    static
    {
        SMALL_FLUID_TANK = new MultiblockDescriptorBase("SMALL_FLUID_TANK", 1, 4, 1, 0, 0, 0);
        KEYPUNCH = new MultiblockDescriptorBase("KEYPUNCH", 2, 1, 1, 0, 0, 0);
        ASSEMBLER = new MultiblockDescriptorBase("ASSEMBLER", 1, 2, 2, 0, 0, 0);
        SOLID_BOILER = new MultiblockDescriptorBase("SOLID_BOILER", 2, 3, 2, 0, 0, 0);

        MEDIUM_FLUID_TANK = new MultiblockDescriptorBase("MEDIUM_FLUID_TANK", 2, 3, 2, 0, 0, 0);
        BIG_FLUID_TANK = new MultiblockDescriptorBase("BIG_FLUID_TANK", 3, 4, 3, 1, 0, 1);
        ROLLING_MILL = new MultiblockDescriptorBase("ROLLING_MILL", 3, 2, 2, 1, 0, 0);
        SOLAR_BOILER = new MultiblockDescriptorBase("SOLAR_BOILER", 3, 6, 3, 1, 0, 1);
        LIQUID_FUEL_BOILER = new MultiblockDescriptorBase("LIQUID_FUEL_BOILER", 2, 3, 2, 0, 0, 0);
        STEAM_FURNACE_MK1 = new MultiblockDescriptorBase("STEAM_FURNACE_MK1", 1, 1, 3, 0, 0, 1);
        STEAM_FURNACE_MK2 = new MultiblockDescriptorBase("STEAM_FURNACE_MK2", 2, 2, 3, 0, 0, 1);
        SOLAR_MIRROR = new MultiblockDescriptorBase("SOLAR_MIRROR", 3, 2, 2, 1, 0, 0);

        SMALL_MINING_DRILL = new MultiblockDescriptorBase("SMALL_MINING_DRILL", 3, 3, 3, 1, 0, 1);
        TINY_MINING_DRILL = new MultiblockDescriptorBase("TINY_MINING_DRILL", 1, 2, 1, 0, 0, 0);
        ORE_WASHER = new MultiblockDescriptorBase("ORE_WASHER", 3, 2, 3, 1, 0, 1);
        SORTING_MACHINE = new MultiblockDescriptorBase("SORTING_MACHINE", 1, 2, 2, 0, 0, 0);

        ALLOY_CAULDRON = new MultiblockDescriptorBase("ALLOY_CAULDRON", 2, 2, 2, 0, 0, 0);
        SAW_MILL = new MultiblockDescriptorBase("SAW_MILL", 1, 1, 1, 0, 0, 0);

        ENGINEER_WORKBENCH = new MultiblockDescriptorBase("ENGINEER_WORKBENCH", 2, 1, 1, 0, 0, 0);
        BLUEPRINT_PRINTER = new MultiblockDescriptorBase("BLUEPRINT_PRINTER", 1, 2, 1, 0, 0, 0);
        CRAFT_CARD_LIBRARY = new MultiblockDescriptorBase("CRAFT_CARD_LIBRARY", 1, 2, 1, 0, 0, 0);
    }
}
