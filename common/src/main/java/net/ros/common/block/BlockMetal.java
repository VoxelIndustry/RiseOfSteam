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
import net.ros.common.recipe.MaterialShape;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.Metal;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;

public class BlockMetal extends BlockBase implements IModelProvider
{
    public static PropertyString FAKE_VARIANTS;

    @Getter
    public final PropertyString variants;

    protected BlockMetal(String name, PropertyString variants)
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
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i,
                        new ModelResourceLocation(block.getRegistryName(), this.getItemModelByIndex(i)));
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
        for (int meta = 0; meta < variants.getAllowedValues().size(); meta++)
            items.add(new ItemStack(this, 1, meta));
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(variants, variants.getByIndex(meta));
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return variants.indexOf(state.getValue(variants));
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
        protected String        name;
        protected MaterialShape type;

        public Builder(String name)
        {
            this.name = name;
        }

        public Builder type(MaterialShape type)
        {
            this.type = type;
            return this;
        }

        public BlockMetal create()
        {
            PropertyString variants = new PropertyString("variant",
                    Materials.metals.stream().filter(metal ->
                            Materials.metals.containsShape(metal, type) &&
                                    !OreDictionary.doesOreNameExist(type.getOreDict() +
                                            StringUtils.capitalize(metal.getName())))
                            .map(Metal::getName).toArray(String[]::new));
            FAKE_VARIANTS = variants;

            return new BlockMetal(name, variants);
        }
    }
}
