package net.qbar.common.block;

import lombok.Getter;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOreDirt extends BlockBase implements IModelProvider
{
    private static final PropertyEnum<BlockOreDirt.EnumType> VARIANTS = PropertyEnum.create("variant", BlockOreDirt.EnumType.class);

    public BlockOreDirt(String name)
    {
        super(name, Material.GROUND);

        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANTS, EnumType.IRON_DIRT));
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
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.getValue(VARIANTS).getMapColor();
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
            for (int i = 0; i < BlockOreClay.EnumType.values().length; i++)
                items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(VARIANTS, BlockOreDirt.EnumType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(VARIANTS).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANTS);
    }

    @Getter
    public enum EnumType implements IStringSerializable
    {
        IRON_DIRT("iron_dirt", MapColor.RED);

        private final String   name;
        private final MapColor mapColor;

        EnumType(String name, MapColor mapColor)
        {
            this.name = name;
            this.mapColor = mapColor;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockOreDirt.EnumType byMetadata(int meta)
        {
            if (meta < values().length)
                return values()[meta];
            return null;
        }
    }
}
