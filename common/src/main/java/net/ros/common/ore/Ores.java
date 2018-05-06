package net.ros.common.ore;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.ros.common.block.BlockVeinOre;
import net.ros.common.init.ROSItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Ores
{
    public static final List<Mineral> MINERALS;

    public static final Mineral IRON;
    public static final Mineral GOLD;
    public static final Mineral REDSTONE;
    public static final Mineral ZINC;
    public static final Mineral COPPER;
    public static final Mineral TIN;
    public static final Mineral NICKEL;
    public static final Mineral LEAD;

    public static final List<Ore> ORES;

    public static final Ore SPHALERITE;

    public static final Ore CHALCOPYRITE;
    public static final Ore MALACHITE;
    public static final Ore TETRAHEDRITE;

    public static final Ore CASSITERITE;
    public static final Ore TEALLITE;

    public static final Ore PENTLANDITE;
    public static final Ore GARNIERITE;
    public static final Ore LATERITE;

    public static final Ore GOLD_ORE;
    public static final Ore REDSTONE_ORE;

    static
    {
        MINERALS = new ArrayList<>();

        IRON = new Mineral("iron", EnumRarity.COMMON);
        GOLD = new Mineral("gold", EnumRarity.UNCOMMON);
        REDSTONE = new Mineral("redstone", EnumRarity.COMMON);
        ZINC = new Mineral("zinc", EnumRarity.COMMON);
        COPPER = new Mineral("copper", EnumRarity.COMMON);
        TIN = new Mineral("tin", EnumRarity.COMMON);
        NICKEL = new Mineral("nickel", EnumRarity.COMMON);
        LEAD = new Mineral("lead", EnumRarity.COMMON);

        MINERALS.addAll(Arrays.asList(IRON, GOLD, REDSTONE, ZINC, COPPER, TIN, NICKEL, LEAD));

        ORES = new ArrayList<>();

        SPHALERITE = Ore.builder().name("sphalerite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(ZINC, 0.6f).mineral(IRON, 0.3f).build();
        CHALCOPYRITE = Ore.builder().name("chalcopyrite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(COPPER, 0.45f).mineral(IRON, 0.3f).build();
        MALACHITE = Ore.builder().name("malachite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(COPPER, 0.3f).build();
        TETRAHEDRITE = Ore.builder().name("tetrahedrite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(COPPER, 0.7f).mineral(IRON, 0.1f).build();
        CASSITERITE = Ore.builder().name("cassiterite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(TIN, 0.8f).build();
        TEALLITE = Ore.builder().name("teallite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(TIN, 0.4f).mineral(LEAD, 0.3f).build();
        PENTLANDITE = Ore.builder().name("pentlandite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(IRON, 0.4f).mineral(NICKEL, 0.4f).build();
        GARNIERITE = Ore.builder().name("garnierite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(IRON, 0.07f).mineral(NICKEL, 0.75f).build();
        LATERITE = Ore.builder().name("laterite")
                .toolLevel(1).hardness(2.0F).resistance(5.0F)
                .mineral(IRON, 0.2f).mineral(NICKEL, 0.7f).build();
        GOLD_ORE = Ore.builder().name("gold")
                .toolLevel(2).hardness(3.0F).resistance(5.0F)
                .mineral(GOLD, 1f).build();
        REDSTONE_ORE = Ore.builder().name("redstone")
                .toolLevel(2).hardness(2.0F).resistance(5.0F)
                .mineral(REDSTONE, 1f).build();

        ORES.addAll(Arrays.asList(SPHALERITE, CHALCOPYRITE, MALACHITE, TETRAHEDRITE, TEALLITE, CASSITERITE, TEALLITE,
                PENTLANDITE, GARNIERITE, LATERITE, GOLD_ORE, REDSTONE_ORE));
    }

    public static Optional<Mineral> getMineralFromName(String name)
    {
        return MINERALS.stream().filter(mineral -> mineral.getName().equalsIgnoreCase(name) ||
                mineral.getName().equalsIgnoreCase(name.substring(4))).findAny();
    }

    public static Optional<Ore> getOreFromState(IBlockState state)
    {
        if (state.getBlock() instanceof BlockVeinOre)
            return getOreFromName(state.getValue(((BlockVeinOre) state.getBlock()).getVARIANTS()));
        return Optional.empty();
    }

    public static Optional<Ore> getOreFromName(String name)
    {
        return ORES.stream().filter(ore -> ore.getName().equals(name)).findAny();
    }

    public static ItemStack getRawMineral(Mineral mineral, MineralDensity density)
    {
        ItemStack rawOre = new ItemStack(ROSItems.RAW_ORE);
        rawOre.setItemDamage((MINERALS.indexOf(mineral) * MineralDensity.VALUES.length) + density.ordinal());
        return rawOre;
    }
}
