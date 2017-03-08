package net.qbar.common.multiblock;

public class Multiblocks
{
    public static final IMultiblockDescriptor FLUID_TANK;
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

    static
    {
        FLUID_TANK = new MultiblockDescriptorBase("FLUID_TANK", 1, 4, 1, 0, 0, 0);
        KEYPUNCH = new MultiblockDescriptorBase("KEYPUNCH", 2, 1, 1, 0, 0, 0);
        ASSEMBLER = new MultiblockDescriptorBase("ASSEMBLER", 2, 2, 1, 0, 0, 0);
        SOLID_BOILER = new MultiblockDescriptorBase("SOLID_BOILER", 3, 3, 3, 0, 0, 0);

        MEDIUM_FLUID_TANK = new MultiblockDescriptorBase("MEDIUM_FLUID_TANK", 2, 3, 2, 0, 0, 0);
        BIG_FLUID_TANK = new MultiblockDescriptorBase("BIG_FLUID_TANK", 3, 4, 3, 0, 0, 0);
        ROLLING_MILL = new MultiblockDescriptorBase("ROLLING_MILL", 2, 2, 3, 0, 0, 0);
        SOLAR_BOILER = new MultiblockDescriptorBase("SOLAR_BOILER", 3, 6, 3, 0, 0, 0);
        LIQUID_FUEL_BOILER = new MultiblockDescriptorBase("LIQUID_FUEL_BOILER", 1, 1, 1, 0, 0, 0);
        STEAM_FURNACE_MK1 = new MultiblockDescriptorBase("STEAM_FURNACE_MK1", 1, 1, 3, 0, 0, 0);
        STEAM_FURNACE_MK2 = new MultiblockDescriptorBase("STEAM_FURNACE_MK2", 1, 1, 1, 0, 0, 0);
    }
}
