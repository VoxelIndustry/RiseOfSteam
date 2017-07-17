package net.qbar.common.ore;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

        SPHALERITE = QBarOre.builder().name("sphalerite").mineral(ZINC, 0.6f).mineral(IRON, 0.3f).build();
        CHALCOPYRITE = QBarOre.builder().name("chalcopyrite").mineral(COPPER, 0.45f).mineral(IRON, 0.3f).build();
        MALACHITE = QBarOre.builder().name("malachite").mineral(COPPER, 0.3f).build();
        TETRAHEDRITE = QBarOre.builder().name("tetrahedrite").mineral(COPPER, 0.7f).mineral(IRON, 0.1f).build();
        CASSITERITE = QBarOre.builder().name("cassiterite").mineral(TIN, 0.8f).build();
        TEALLITE = QBarOre.builder().name("teallite").mineral(TIN, 0.4f).mineral(LEAD, 0.3f).build();
        PENTLANDITE = QBarOre.builder().name("pentlandite").mineral(IRON, 0.4f).mineral(NICKEL, 0.4f).build();
        GARNIERITE = QBarOre.builder().name("garnierite").mineral(IRON, 0.07f).mineral(NICKEL, 0.75f).build();
        LATERITE = QBarOre.builder().name("laterite").mineral(IRON, 0.2f).mineral(NICKEL, 0.7f).build();

        ORES.addAll(Arrays.asList(SPHALERITE, CHALCOPYRITE, MALACHITE, TEALLITE, CASSITERITE, TEALLITE, PENTLANDITE,
                GARNIERITE, LATERITE));
    }

    public static Optional<QBarMineral> getMineralFromName(String name)
    {
        return MINERALS.stream().filter(mineral -> mineral.getName().equalsIgnoreCase(name)).findAny();
    }

    public static Optional<QBarOre> getOreFromState(IBlockState state)
    {
        if (state.getBlock() instanceof BlockVeinOre)
            return getOreFromName(state.getValue(((BlockVeinOre) state.getBlock()).getVARIANTS()));
        return Optional.empty();
    }

    public static Optional<QBarOre> getOreFromName(String name)
    {
        return ORES.stream().filter(ore -> ore.getName().equalsIgnoreCase(name)).findAny();
    }

    public static ItemStack getRawMineral(QBarMineral mineral)
    {
        ItemStack rawOre = new ItemStack(QBarItems.RAW_ORE);
        rawOre.setTagCompound(new NBTTagCompound());

        rawOre.getTagCompound().setString("ore", mineral.getName());
        return rawOre;
    }
}
