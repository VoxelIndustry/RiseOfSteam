package net.qbar.common.ore;

import net.minecraft.item.EnumRarity;

public class QBarOre
{
    private final String     name;
    private final EnumRarity rarity;

    public QBarOre(String name, EnumRarity rarity)
    {
        this.name = name;
        this.rarity = rarity;
    }

    public String getName()
    {
        return name;
    }

    public EnumRarity getRarity()
    {
        return rarity;
    }
}