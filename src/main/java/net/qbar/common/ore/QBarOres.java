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
    public static final List<QBarOre> ORES;

    public static final QBarOre       IRON;
    public static final QBarOre       GOLD;
    public static final QBarOre       REDSTONE;
    public static final QBarOre       ZINC;
    public static final QBarOre       COPPER;
    public static final QBarOre       TIN;
    public static final QBarOre       NICKEL;

    static
    {
        ORES = new ArrayList<>();

        IRON = new QBarOre("ore.iron", EnumRarity.COMMON);
        GOLD = new QBarOre("ore.gold", EnumRarity.UNCOMMON);
        REDSTONE = new QBarOre("ore.redstone", EnumRarity.COMMON);
        ZINC = new QBarOre("ore.zinc", EnumRarity.COMMON);
        COPPER = new QBarOre("ore.copper", EnumRarity.COMMON);
        TIN = new QBarOre("ore.tin", EnumRarity.COMMON);
        NICKEL = new QBarOre("ore.nickel", EnumRarity.COMMON);

        ORES.addAll(Arrays.asList(IRON, GOLD, REDSTONE, ZINC, COPPER, TIN, NICKEL));
    }

    public static Optional<QBarOre> getOreFromName(String name)
    {
        return ORES.stream().filter(ore -> ore.getName().equalsIgnoreCase(name)).findAny();
    }

    public static ItemStack getRawOre(QBarOre ore)
    {
        ItemStack rawOre = new ItemStack(QBarItems.RAW_ORE);
        rawOre.setTagCompound(new NBTTagCompound());

        rawOre.getTagCompound().setString("ore", ore.getName());
        return rawOre;
    }
}
