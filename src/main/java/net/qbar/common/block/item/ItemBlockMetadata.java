package net.qbar.common.block.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMetadata extends ItemBlock
{
    private final String[] variants;

    public ItemBlockMetadata(final Block block, final String... variants)
    {
        super(block);
        this.setHasSubtypes(true);

        this.variants = variants;
    }

    @Override
    public int getMetadata(final int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack)
    {
        if (stack.getMetadata() != 0 && stack.getMetadata() <= this.variants.length)
            return this.getUnlocalizedName() + "." + this.variants[stack.getMetadata() - 1];
        return this.getUnlocalizedName();
    }
}
