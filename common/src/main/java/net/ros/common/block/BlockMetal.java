package net.ros.common.block;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.ros.common.block.property.PropertyString;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.Metal;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;

public class BlockMetal extends BlockBase implements IModelProvider
{
    private static PropertyString FAKE_VARIANTS;

    @Getter
    public final PropertyString variants;

    private BlockMetal(String name, PropertyString variants)
    {
        super(name, Material.IRON);

        this.variants = variants;
        this.setDefaultState(this.blockState.getBaseState().withProperty(variants, variants.getByIndex(0)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getItemModelCount()
    {
        return variants.getAllowedValues().size();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemModelByIndex(int index)
    {
        return "variant=" + this.getStateFromMeta(index).getValue(variants);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BiConsumer<Integer, Block> registerItemModels()
    {
        return (i, block) ->
        {
            Materials.metals.byName(variants.getByIndex(i)).ifPresent(metal ->
            {
                int index = Materials.metals.indexOf(metal);

                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), index,
                        new ModelResourceLocation(
                                block.getRegistryName(), this.getItemModelByIndex(index)));
            });
        };
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return this.getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        for (int i = 0; i < Materials.metals.size(); i++)
        {
            if (variants.getAllowedValues().contains(Materials.metals.get(i)))
                items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(variants, Materials.metals.get(meta).getName());
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return Materials.metals.indexOf(Materials.metals.byName(state.getValue(variants)).get());
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FAKE_VARIANTS);
    }

    public static Builder build(String name)
    {
        return new Builder(name);
    }

    public static class Builder
    {
        private String name;
        private String type;

        public Builder(String name)
        {
            this.name = name;
        }

        public Builder type(String type)
        {
            this.type = type;
            return this;
        }

        public BlockMetal create()
        {
            PropertyString variants = new PropertyString("variant",
                    Materials.metals.stream().filter(metal ->
                            !OreDictionary.doesOreNameExist(type + StringUtils.capitalize(metal.getName())))
                            .map(Metal::getName).toArray(String[]::new));
            FAKE_VARIANTS = variants;

            return new BlockMetal(name, variants);
        }
    }
}
