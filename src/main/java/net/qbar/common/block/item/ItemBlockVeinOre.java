package net.qbar.common.block.item;

import net.qbar.common.block.BlockVeinOre;

import java.util.stream.Stream;

public class ItemBlockVeinOre extends ItemBlockMetadata
{
    public ItemBlockVeinOre(BlockVeinOre veinOre)
    {
        super(veinOre,
                veinOre.getVARIANTS().getAllowedValues().stream()
                        .flatMap(variant -> Stream.of(variant + ".poor", variant + ".normal", variant + ".rich"))
                        .toArray(String[]::new));

        this.setFirstVariation(true);
    }
}
