package net.ros.common.block.item;

import net.ros.common.block.BlockVeinOre;

import java.util.ArrayList;
import java.util.List;

public class ItemBlockVeinOre extends ItemBlockMetadata
{
    public ItemBlockVeinOre(BlockVeinOre veinOre)
    {
        super(veinOre, getVariants(veinOre));

        this.setFirstVariation(true);
    }

    private static String[] getVariants(BlockVeinOre veinOre)
    {
        List<String> variants = new ArrayList<>();

        veinOre.getVARIANTS().getAllowedValues().forEach(variant -> variants.add(variant + ".poor"));
        veinOre.getVARIANTS().getAllowedValues().forEach(variant -> variants.add(variant + ".normal"));
        veinOre.getVARIANTS().getAllowedValues().forEach(variant -> variants.add(variant + ".rich"));
        return variants.toArray(new String[variants.size()]);
    }
}