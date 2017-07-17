package net.qbar.common.block.item;

import net.qbar.common.block.BlockVeinOre;
import net.qbar.common.ore.QBarOre;

public class ItemBlockVeinOre extends ItemBlockMetadata
{
    public ItemBlockVeinOre(BlockVeinOre veinOre)
    {
        super(veinOre, veinOre.getContents().stream().map(QBarOre::getName).toArray(String[]::new));

        this.setFirstVariation(true);
    }
}
