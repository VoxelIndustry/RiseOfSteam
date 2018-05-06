package net.ros.common.ore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.item.EnumRarity;

@Getter
@ToString
@EqualsAndHashCode
public class Mineral implements Comparable<Mineral>
{
    private final String     name;
    private final String     nameID;
    private final EnumRarity rarity;

    public Mineral(String name, EnumRarity rarity)
    {
        this.name = "ore." + name;
        this.nameID = name;
        this.rarity = rarity;
    }

    @Override
    public int compareTo(Mineral other)
    {
        return name.compareTo(other.getName());
    }
}