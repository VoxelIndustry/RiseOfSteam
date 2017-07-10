package net.qbar.common.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.qbar.common.block.property.PropertyString;
import net.qbar.common.ore.SludgeData;

public class BlockVeinOre extends BlockBase
{
    @Getter
    private BiMap<String, SludgeData> contents;

    private static PropertyString     FAKE_VARIANTS;
    private final PropertyString      VARIANTS;

    public BlockVeinOre(String name, String defaultValue, BiMap<String, SludgeData> contents, PropertyString variants)
    {
        super(name, Material.ROCK);

        this.VARIANTS = variants;
        this.contents = contents;

        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANTS, defaultValue));
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
            for (int i = 0; i < contents.size(); i++)
                items.add(new ItemStack(this, 1, i));
        }
    }

    public IBlockState getStateFromOre(String oreName)
    {
        return this.getDefaultState().withProperty(VARIANTS, oreName);
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
        return new BlockStateContainer(this, FAKE_VARIANTS);
    }

    public static class Builder
    {
        private BiMap<String, SludgeData> contents = HashBiMap.create();
        private String                    name;
        private String                    defaultValue;

        public Builder(String name)
        {
            this.name = name;
        }

        public Builder addContent(String variant, SludgeData data)
        {
            if (defaultValue == null)
                this.defaultValue = variant;
            this.contents.put(variant, data);
            return this;
        }

        public BlockVeinOre create()
        {
            PropertyString variants = new PropertyString("ores");
            variants.addValues(contents.keySet().toArray(new String[contents.size()]));
            FAKE_VARIANTS = variants;

            return new BlockVeinOre(name, defaultValue, contents, variants);
        }
    }
}
