package net.qbar.common.ore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.item.EnumRarity;

@Getter
@ToString
@EqualsAndHashCode
public class QBarMineral implements Comparable<QBarMineral>
{
    private final String     name;
    private final String     nameID;
    private final EnumRarity rarity;

    public QBarMineral(String name, EnumRarity rarity)
    {
        this.name = "ore." + name;
        this.nameID = name;
        this.rarity = rarity;
    }

    @Override
    public int compareTo(QBarMineral other)
    {
        return name.compareTo(other.getName());
    }
}