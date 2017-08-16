package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.qbar.common.block.property.PropertyString;
import net.qbar.common.recipe.QBarMaterials;
import org.apache.commons.lang3.StringUtils;

public class BlockMetal extends BlockBase implements IModelProvider
{
    public static PropertyString VARIANTS = new PropertyString("variant",
            QBarMaterials.metals.stream().filter(metal ->
                    !OreDictionary.doesOreNameExist("block" + StringUtils.capitalize(metal))).toArray(String[]::new));

    public BlockMetal()
    {
        super("blockmetal", Material.IRON);

        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANTS, VARIANTS.getByIndex(0)));

    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getItemModelCount()
    {
        return VARIANTS.getAllowedValues().size();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemModelFromMeta(int itemMeta)
    {
        return "variant=" + this.getStateFromMeta(itemMeta).getValue(VARIANTS);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return this.getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        for (int i = 0; i < VARIANTS.getAllowedValues().size(); i++)
            items.add(new ItemStack(this, 1, i));
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
