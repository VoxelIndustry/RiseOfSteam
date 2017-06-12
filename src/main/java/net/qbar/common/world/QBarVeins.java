package net.qbar.common.world;

public class QBarVeins
{
    public static OreVeinDescriptor IRON_NICKEL;
    public static OreVeinDescriptor IRON_COPPER;
    public static OreVeinDescriptor TIN;
    public static OreVeinDescriptor IRON_ZINC;
    public static OreVeinDescriptor GOLD;
    public static OreVeinDescriptor REDSTONE;

    public static final void initVeins()
    {
        IRON_NICKEL = new OreVeinDescriptor();
        IRON_COPPER = new OreVeinDescriptor();
        TIN = new OreVeinDescriptor();
        IRON_ZINC = new OreVeinDescriptor();
        GOLD = new OreVeinDescriptor();
        REDSTONE = new OreVeinDescriptor();
    }
}
