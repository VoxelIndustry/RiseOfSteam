package net.qbar.common.block.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMetadata extends ItemBlock
{
    private final String[] variants;
    private boolean        firstVariation;

    public ItemBlockMetadata(final Block block, final String... variants)
    {
        super(block);
        this.setHasSubtypes(true);

        this.variants = variants;
    }

    public ItemBlockMetadata setFirstVariation(boolean firstVariation)
    {
        this.firstVariation = firstVariation;
        return this;
    }

    @Override
    public int getMetadata(final int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack)
    {
        if ((firstVariation || stack.getMetadata() != 0) && stack.getMetadata() <= this.variants.length)
            return this.getUnlocalizedName() + "." + this.variants[stack.getMetadata() - (firstVariation ? 0 : 1)];
        return this.getUnlocalizedName();
    }
}
