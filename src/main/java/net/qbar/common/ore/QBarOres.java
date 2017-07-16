package net.qbar.common.ore;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.common.init.QBarItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class QBarOres
{
    public static final List<QBarMineral> MINERALS;

    public static final QBarMineral       IRON;
    public static final QBarMineral       GOLD;
    public static final QBarMineral       REDSTONE;
    public static final QBarMineral       ZINC;
    public static final QBarMineral       COPPER;
    public static final QBarMineral       TIN;
    public static final QBarMineral       NICKEL;
    public static final QBarMineral       LEAD;

    public static final List<QBarOre>     ORES;

    public static final QBarOre           SPHALERITE;

    public static final QBarOre           CHALCOPYRITE;
    public static final QBarOre           MALACHITE;
    public static final QBarOre           TETRAHEDRITE;

    public static final QBarOre           CASSITERITE;
    public static final QBarOre           TEALLITE;

    public static final QBarOre           PENTLANDITE;
    public static final QBarOre           GARNIERITE;
    public static final QBarOre           LATERITE;

    static
    {
        MINERALS = new ArrayList<>();

        IRON = new QBarMineral("ore.iron", EnumRarity.COMMON);
        GOLD = new QBarMineral("ore.gold", EnumRarity.UNCOMMON);
        REDSTONE = new QBarMineral("ore.redstone", EnumRarity.COMMON);
        ZINC = new QBarMineral("ore.zinc", EnumRarity.COMMON);
        COPPER = new QBarMineral("ore.copper", EnumRarity.COMMON);
        TIN = new QBarMineral("ore.tin", EnumRarity.COMMON);
        NICKEL = new QBarMineral("ore.nickel", EnumRarity.COMMON);
        LEAD = new QBarMineral("ore.lead", EnumRarity.COMMON);

        MINERALS.addAll(Arrays.asList(IRON, GOLD, REDSTONE, ZINC, COPPER, TIN, NICKEL, LEAD));

        ORES = new ArrayList<>();

        SPHALERITE = new QBarOre("sphalerite", ZINC, IRON);
        CHALCOPYRITE = new QBarOre("chalcopyrite", COPPER, IRON);
        MALACHITE = new QBarOre("malachite", COPPER);
        TETRAHEDRITE = new QBarOre("tetrahedrite", COPPER, IRON);
        CASSITERITE = new QBarOre("cassiterite", TIN);
        TEALLITE = new QBarOre("teallite", TIN, LEAD);
        PENTLANDITE = new QBarOre("pentlandite", IRON, NICKEL);
        GARNIERITE = new QBarOre("garnierite", IRON, NICKEL);
        LATERITE = new QBarOre("laterite", IRON, NICKEL);

        ORES.addAll(Arrays.asList(SPHALERITE, CHALCOPYRITE, MALACHITE, TEALLITE, CASSITERITE, TEALLITE, PENTLANDITE,
                GARNIERITE, LATERITE));
    }

    public static Optional<QBarMineral> getMineralFromName(String name)
    {
        return MINERALS.stream().filter(ore -> ore.getName().equalsIgnoreCase(name)).findAny();
    }

    public static ItemStack getRawMineral(QBarMineral mineral)
    {
        ItemStack rawOre = new ItemStack(QBarItems.RAW_ORE);
        rawOre.setTagCompound(new NBTTagCompound());

        rawOre.getTagCompound().setString("ore", mineral.getName());
        return rawOre;
    }
}
