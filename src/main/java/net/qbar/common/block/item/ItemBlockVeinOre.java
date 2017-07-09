package net.qbar.common.block.item;

import net.qbar.common.block.BlockVeinOre;

public class ItemBlockVeinOre extends ItemBlockMetadata
{
    public ItemBlockVeinOre(BlockVeinOre veinOre)
    {
        super(veinOre, veinOre.getContents().keySet().toArray(new String[veinOre.getContents().size()]));

        this.setFirstVariation(true);
    }
}
