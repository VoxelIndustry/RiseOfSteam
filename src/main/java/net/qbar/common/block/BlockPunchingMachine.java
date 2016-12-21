package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.qbar.QBar;

/**
 * @author Ourten 21 déc. 2016
 */
public class BlockPunchingMachine extends Block
{
    public BlockPunchingMachine()
    {
        super(Material.IRON);
        this.setUnlocalizedName("BlockPunchingMachine");
        this.setCreativeTab(QBar.TAB_ALL);
    }

}
