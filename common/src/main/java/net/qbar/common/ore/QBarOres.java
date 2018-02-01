package net.qbar.common.ore;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.qbar.common.block.BlockVeinOre;
import net.qbar.common.init.QBarItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class QBarOres
{
    public static final List<QBarMineral> MINERALS;

    public static final QBarMineral IRON;
    public static final QBarMineral GOLD;
    public static final QBarMineral REDSTONE;
    public static final QBarMineral ZINC;
    public static final QBarMineral COPPER;
    public static final QBarMineral TIN;
    public static final QBarMineral NICKEL;
    public static final QBarMineral LEAD;

    public static final List<QBarOre> ORES;

    public static final QBarOre SPHALERITE;

    public static final QBarOre CHALCOPYRITE;
    public static final QBarOre MALACHITE;
    public static final QBarOre TETRAHEDRITE;

    public static final QBarOre CASSITERITE;
    public static final QBarOre TEALLITE;

    public static final QBarOre PENTLANDITE;
    public static final QBarOre GARNIERITE;
    public static final QBarOre LATERITE;

    public static final QBarOre GOLD_ORE;
    public static final QBarOre REDSTONE_ORE;

    static
    {
        MINERALS = new ArrayList<>();

        IRON = new QBarMineral("iron", EnumRarity.COMMON);
        GOLD = new QBarMineral("gold", EnumRarity.UNCOMMON);
        REDSTONE = new QBarMineral("redstone", EnumRarity.COMMON);
        ZINC = new QBarMineral("zinc", EnumRarity.COMMON);
        COPPER = new QBarMineral("copper", EnumRarity.COMMON);
        TIN = new QBarMineral("tin", EnumRarity.COMMON);
        NICKEL = new QBarMineral("nickel", EnumRarity.COMMON);
        LEAD = new QBarMineral("lead", EnumRarity.COMMON);

        MINERALS.addAll(Arrays.asList(IRON, GOLD, REDSTONE, ZINC, COPPER, TIN, NICKEL, LEAD));

        ORES = new ArrayList<>();

        SPHALERITE = QBarOre.builder().name("sphalerite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(ZINC, 0.6f).mineral(IRON, 0.3f).build();
        CHALCOPYRITE = QBarOre.builder().name("chalcopyrite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(COPPER, 0.45f).mineral(IRON, 0.3f).build();
        MALACHITE = QBarOre.builder().name("malachite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(COPPER, 0.3f).build();
        TETRAHEDRITE = QBarOre.builder().name("tetrahedrite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(COPPER, 0.7f).mineral(IRON, 0.1f).build();
        CASSITERITE = QBarOre.builder().name("cassiterite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(TIN, 0.8f).build();
        TEALLITE = QBarOre.builder().name("teallite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(TIN, 0.4f).mineral(LEAD, 0.3f).build();
        PENTLANDITE = QBarOre.builder().name("pentlandite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(IRON, 0.4f).mineral(NICKEL, 0.4f).build();
        GARNIERITE = QBarOre.builder().name("garnierite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(IRON, 0.07f).mineral(NICKEL, 0.75f).build();
        LATERITE = QBarOre.builder().name("laterite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(IRON, 0.2f).mineral(NICKEL, 0.7f).build();
        GOLD_ORE = QBarOre.builder().name("gold")
                .toolLevel(2).hardness(3.0F).resistance(5.0F)
                .mineral(GOLD, 1f).build();
        REDSTONE_ORE = QBarOre.builder().name("redstone")
                .toolLevel(2).hardness(2.0F).resistance(5.0F)
                .mineral(REDSTONE, 1f).build();

        ORES.addAll(Arrays.asList(SPHALERITE, CHALCOPYRITE, MALACHITE, TETRAHEDRITE, TEALLITE, CASSITERITE, TEALLITE,
                PENTLANDITE, GARNIERITE, LATERITE, GOLD_ORE, REDSTONE_ORE));
    }

    public static Optional<QBarMineral> getMineralFromName(String name)
    {
        return MINERALS.stream().filter(mineral -> mineral.getName().equalsIgnoreCase(name) ||
                mineral.getName().equalsIgnoreCase(name.substring(4))).findAny();
    }

    public static Optional<QBarOre> getOreFromState(IBlockState state)
    {
        if (state.getBlock() instanceof BlockVeinOre)
            return getOreFromName(state.getValue(((BlockVeinOre) state.getBlock()).getVARIANTS()));
        return Optional.empty();
    }

    public static Optional<QBarOre> getOreFromName(String name)
    {
        return ORES.stream().filter(ore -> ore.getName().equals(name)).findAny();
    }

    public static ItemStack getRawMineral(QBarMineral mineral, MineralDensity density)
    {
        ItemStack rawOre = new ItemStack(QBarItems.RAW_ORE);
        rawOre.setItemDamage((MINERALS.indexOf(mineral) * MineralDensity.VALUES.length) + density.ordinal());
        return rawOre;
    }
}
