package net.qbar.common.block.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockExtractor extends ItemBlock
{
    public ItemBlockExtractor(final Block block)
    {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(final int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack)
    {
        if (stack.getMetadata() == 1)
            return this.getUnlocalizedName() + "filtered";
        return this.getUnlocalizedName();
    }
}
