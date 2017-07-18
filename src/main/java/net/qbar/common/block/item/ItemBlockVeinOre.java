package net.qbar.common.block.item;

import net.qbar.common.block.BlockVeinOre;

public class ItemBlockVeinOre extends ItemBlockMetadata
{
    public ItemBlockVeinOre(BlockVeinOre veinOre)
    {
        super(veinOre, veinOre.getVARIANTS().getAllowedValues().toArray(new String[veinOre.getVARIANTS().getAllowedValues().size()]));

        this.setFirstVariation(true);
    }
}
