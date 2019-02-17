package net.ros.common.block.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMetadata extends ItemBlock
{
    private final String[] variants;
    private       boolean  firstVariation;

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
    public String getTranslationKey(final ItemStack stack)
    {
        int offset = firstVariation ? 0 : 1;

        if ((firstVariation || stack.getMetadata() != 0) && stack.getMetadata() - offset < this.variants.length)
            return this.getTranslationKey() + "." + this.variants[stack.getMetadata() - offset];
        return this.getTranslationKey();
    }
}
