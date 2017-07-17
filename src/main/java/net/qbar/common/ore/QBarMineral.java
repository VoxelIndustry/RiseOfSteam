package net.qbar.common.ore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.item.EnumRarity;

@Getter
@ToString
@AllArgsConstructor
public class QBarMineral
{
    private final String     name;
    private final EnumRarity rarity;
}