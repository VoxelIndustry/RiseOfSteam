package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.qbar.common.block.property.PropertyString;

public class BlockOreDirt extends BlockBase
{
    private static PropertyString VARIANTS = new PropertyString("type", "iron_dirt");

    public BlockOreDirt(String name)
    {
        super(name, Material.GROUND);

        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANTS, "iron_dirt"));
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return this.getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (tab == this.getCreativeTabToDisplayOn())
        {
            for (int i = 0; i < VARIANTS.getAllowedValues().size(); i++)
                items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(VARIANTS, VARIANTS.getByIndex(meta));
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return VARIANTS.indexOf(state.getValue(VARIANTS));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANTS);
    }
}
