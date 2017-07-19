package net.qbar.common.ore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.item.EnumRarity;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class QBarMineral implements Comparable<QBarMineral>
{
    private final String     name;
    private final EnumRarity rarity;

    @Override
    public int compareTo(QBarMineral other)
    {
        return name.compareTo(other.getName());
    }
}