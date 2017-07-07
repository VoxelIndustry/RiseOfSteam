package net.qbar.common.ore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.EnumRarity;

@Getter
@AllArgsConstructor
public class QBarOre
{
    private final String     name;
    private final EnumRarity rarity;
}